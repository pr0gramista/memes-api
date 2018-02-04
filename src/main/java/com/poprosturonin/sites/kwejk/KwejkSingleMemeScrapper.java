package com.poprosturonin.sites.kwejk;

import com.poprosturonin.data.Author;
import com.poprosturonin.data.Comment;
import com.poprosturonin.data.Meme;
import com.poprosturonin.data.Tag;
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

    private final static String KWEJK_COMMENTS_URL = "https://kwejk.pl/comment/get";

    private boolean is404(String title) {
        return title.contains(SEQUENCE_404);
    }

    private Optional<Meme> parseMemeBlock(Element block) {
        String title = null;
        String url = null;
        int commentAmount = 0;
        int votes = 0;

        // Get title
        Element titleElement = block.select(".content > h2 > a").first();
        if (titleElement != null) {
            title = titleElement.text();
            url = titleElement.attr("href");
        }

        //If no header was found, skip this article
        if (title == null || url == null)
            return Optional.empty();

        //Get author
        Author author = null;
        Element authorElement = block.select("div.user-bar > div.content > a").first();
        if (authorElement != null) {
            author = new Author(
                    authorElement.getElementsByClass("name").first().text(),
                    authorElement.attr("href"));
        }

        //Get comments and votes
        try {
            commentAmount = Integer.parseInt(block.attr("data-comments-count"));
        } catch (NumberFormatException e) {
            commentAmount = 0;
        }

        try {
            votes = Integer.parseInt(block.attr("data-vote-up")) - Integer.parseInt(block.attr("data-vote-down"));
        } catch (NumberFormatException e) {
            votes = 0;
        }

        //Get tags
        Elements tagElements = block.select("div.tag-list > a");
        List<Tag> tags = null;
        if (!tagElements.isEmpty()) {
            tags = tagElements.stream().map(e ->
                    new Tag(e.text().replace("#", ""),
                            e.attr("href"),
                            URLUtils.cutToSecondSlash(e.attr("href")).orElse("  ").substring(1)
                    )
            ).collect(Collectors.toList());
        }

        //Get comments
        List<Comment> comments = null; //etComments();


        Optional<GalleryContent> galleryContent = tryToParseAsGalleryContent(block);
        if (galleryContent.isPresent()) {
            Meme meme = new Meme(title, galleryContent.get(), url, commentAmount, votes);
            meme.setAuthor(author);
            meme.setTags(tags);
            meme.setComments(comments);
            return Optional.of(meme);
        }

        Optional<VideoContent> videoContent = tryToParseAsVideoContent(block);
        if (videoContent.isPresent()) {
            Meme meme = new Meme(title, videoContent.get(), url, commentAmount, votes);
            meme.setAuthor(author);
            meme.setTags(tags);
            meme.setComments(comments);
            return Optional.of(meme);
        }

        Optional<ImageContent> imageContent = tryToParseAsImageContent(block);
        if (imageContent.isPresent()) {
            Meme meme = new Meme(title, imageContent.get(), url, commentAmount, votes);
            meme.setAuthor(author);
            meme.setTags(tags);
            meme.setComments(comments);
            return Optional.of(meme);
        }

        return Optional.empty();
    }

    private Optional<ImageContent> tryToParseAsImageContent(Element article) {
        Optional<Element> image = Optional.ofNullable(article.select("figure > a > img").first());
        return image.map(element -> new ImageContent(element.attr("src")));
    }

    private Optional<VideoContent> tryToParseAsVideoContent(Element article) {
        Optional<Element> video = Optional.ofNullable(article.getElementsByTag("video").first());
        return video.map(element -> new VideoContent(element.attr("src")));
    }

    private Optional<GalleryContent> tryToParseAsGalleryContent(Element block) {
        Elements thumbnails = block.select(".slider-nav > li > a > img");
        if (thumbnails.isEmpty())
            return Optional.empty();

        List<String> urls = thumbnails.stream()
                .map(element -> element.attr("src").replace("_thumb", ""))
                .collect(Collectors.toList());
        return Optional.of(new GalleryContent(urls));
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
        Element article = document.select(".media-element").first();
        if (article == null)
            throw new CouldNotParseMemeException();

        Optional<Meme> memeOptional = parseMemeBlock(article);
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
