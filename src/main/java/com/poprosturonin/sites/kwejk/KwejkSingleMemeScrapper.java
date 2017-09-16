package com.poprosturonin.sites.kwejk;

import com.poprosturonin.data.Author;
import com.poprosturonin.data.Comment;
import com.poprosturonin.data.Meme;
import com.poprosturonin.data.contents.GalleryContent;
import com.poprosturonin.data.contents.ImageContent;
import com.poprosturonin.data.contents.VideoContent;
import com.poprosturonin.exceptions.CouldNotParseMemeException;
import com.poprosturonin.exceptions.MemeSiteResponseFailedException;
import com.poprosturonin.sites.SingleMemeScrapper;
import com.poprosturonin.utils.URLUtils;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class KwejkSingleMemeScrapper implements SingleMemeScrapper {
    private final static String SEQUENCE_404 = "404 - strona";

    private final static String KWEJK_COMMENTS_URL = "https://kwejk.pl/comments/ajax/load";

    private boolean is404(String title) {
        return title.contains(SEQUENCE_404);
    }

    private List<String> parseGallery(Element article) {
        List<String> list = new ArrayList<>(25);
        Elements thumbnails = article.select(".jcarousel img");
        thumbnails.forEach((Element thumbnail) -> list.add(thumbnail.attr("src").replace("_thumb", "")));
        return list;
    }

    private Optional<Meme> parseArticle(Element article) {
        String title = null;
        String url = null;
        String absUrl = null;
        int comments = 0;
        int votes = 0;

        //Get header
        Elements headers = article.select("h1 > art-ah0 > a");
        if (headers.size() > 0) {
            Element headerElement = headers.get(0);
            title = headerElement.text();
            absUrl = headerElement.attr("href");
            url = URLUtils.cutToSecondSlash(absUrl).orElse(null); // Optional TODO
        }

        //Get author
        Element authorElement = article.select(".author > .info > a").first();
        String nick = authorElement.text().trim();
        String profile_url = authorElement.attr("href");
        Author author = new Author(nick, profile_url);

        //Get comments
        Elements commentElements = article.select(".comments-num");
        if (commentElements.size() > 0) {
            Element commentElement = commentElements.get(0);
            comments = Integer.parseInt(commentElement.text());
        }

        //Get votes
        Elements voteElements = article.select(".votes > span.bubble");
        if (voteElements.size() > 0) {
            Element voteElement = voteElements.get(0);
            votes = Integer.parseInt(voteElement.text());
        }

        //If no header was found, skip this article
        if (title == null || url == null)
            return Optional.empty();

        //Get id (for comments)
        Element idHolder = article.select("div.actions").first();
        String id = idHolder.attr("data-id");

        Meme meme = new Meme();
        meme.setTitle(title);
        meme.setUrl(url);
        meme.setPoints(votes);
        meme.setCommentAmount(comments);
        meme.setAuthor(author);
        meme.setComments(getComments(id));

        Optional<GalleryContent> galleryContent = tryToParseAsGalleryContent(article, absUrl);
        if (galleryContent.isPresent()) {
            meme.setContent(galleryContent.get());
            return Optional.of(meme);
        }

        Optional<VideoContent> videoContent = tryToParseAsVideoContent(article);
        if (videoContent.isPresent()) {
            meme.setContent(videoContent.get());
            return Optional.of(meme);
        }

        Optional<ImageContent> imageContent = tryToParseAsImageContent(article);
        if (imageContent.isPresent()) {
            meme.setContent(imageContent.get());
            return Optional.of(meme);
        }

        return Optional.empty();
    }

    private Optional<ImageContent> tryToParseAsImageContent(Element article) {
        Elements images = article.select(".object img");
        if (images.size() > 0) {
            Element contentElement = images.get(0);
            return Optional.of(new ImageContent(contentElement.attr("src")));
        } else
            return Optional.empty();
    }

    private Optional<VideoContent> tryToParseAsVideoContent(Element article) {
        Elements videos = article.select(".object video");
        if (videos.size() > 0) {
            Element contentElement = videos.get(0);
            return Optional.of(new VideoContent(contentElement.attr("src")));
        } else
            return Optional.empty();
    }

    private Optional<GalleryContent> tryToParseAsGalleryContent(Element article, String headerURL) {
        if (headerURL.contains("/przegladaj/")) {
            return Optional.of(new GalleryContent(parseGallery(article)));
        } else
            return Optional.empty();
    }

    private List<Comment> getComments(String id) {
        // TODO: handle multiple pages of comments
        JSONObject response = new JSONObject(getCommentJSON(id));
        String commentsHTML = response.getString("html");
        Document commentDocument = Jsoup.parse(commentsHTML);

        List<KwejkComment> comments = new ArrayList<>(30);

        Elements commentElements = commentDocument.select("article");
        for (Element commentElement : commentElements) {
            Element authorElement = commentElement.select(".info > div").first().child(0);
            Author author = null;
            System.out.println(authorElement.tag().getName());
            if (authorElement.tag().getName().equals("a")) {
                author = new Author(authorElement.attr("title").trim(), authorElement.attr("href"));
            }

            String content = commentElement.select("div.p").first().text().trim();
            int points = Integer.parseInt(commentElement.select(".votes > span[data-container=\"bubble\"]").text());
            int cid = Integer.parseInt(commentElement.attr("data-id"));
            int parent_cid = Integer.parseInt(commentElement.attr("data-parent-id"));

            KwejkComment kwejkComment = new KwejkComment(content, author, points);
            kwejkComment.setId(cid);

            if (parent_cid != 0) {
                for (KwejkComment comment : comments) {
                    if (comment.getId() == parent_cid) {
                        comment.getResponses().add(kwejkComment);
                        break;
                    }
                }
            } else {
                comments.add(kwejkComment);
            }
        }

        return comments.stream().map(kwejkComment -> (Comment) kwejkComment).collect(Collectors.toList());
    }

    private String getCommentJSON(String id) {
        try {
            String data = "id=" + id + "&page=1"; //fe. id=3039657
            String type = "application/x-www-form-urlencoded";
            URL downloadURL = new URL(KWEJK_COMMENTS_URL);
            HttpURLConnection connection = (HttpURLConnection) downloadURL.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("x-requested-with", "XMLHttpRequest");
            connection.setRequestProperty("Content-Type", type);
            connection.setRequestProperty("Content-Length", String.valueOf(data.length()));
            OutputStream os = connection.getOutputStream();
            os.write(data.getBytes());

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
            StringBuffer buffer = new StringBuffer();
            int read;
            char[] chars = new char[1024];
            while ((read = reader.read(chars)) != -1)
                buffer.append(chars, 0, read);

            return buffer.toString();
        } catch (IOException e) {
            e.printStackTrace();
            throw new MemeSiteResponseFailedException();
        }
    }

    @Override
    public Optional<Meme> parseMeme(Document document) {
        Element article = document.select("article.content").first();
        if (article == null)
            throw new CouldNotParseMemeException();

        Optional<Meme> memeOptional = parseArticle(article);
        if (!memeOptional.isPresent())
            throw new CouldNotParseMemeException();

        return memeOptional;
    }

    private class KwejkComment extends Comment {
        private int id;

        public KwejkComment(String content, Author author, int points) {
            super(content, author, points);
        }

        public KwejkComment(String content, Author author, List<Comment> responses, int points) {
            super(content, author, responses, points);
        }

        private int getId() {
            return id;
        }

        private void setId(int id) {
            this.id = id;
        }
    }
}
