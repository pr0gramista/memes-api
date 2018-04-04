package com.poprosturonin.sites.jbzd;

import com.poprosturonin.data.Author;
import com.poprosturonin.data.Comment;
import com.poprosturonin.data.Meme;
import com.poprosturonin.data.Tag;
import com.poprosturonin.data.contents.Content;
import com.poprosturonin.data.contents.GIFContent;
import com.poprosturonin.data.contents.ImageContent;
import com.poprosturonin.data.contents.VideoContent;
import com.poprosturonin.exceptions.CouldNotParseMemeException;
import com.poprosturonin.exceptions.MemeSiteResponseFailedException;
import com.poprosturonin.sites.SingleMemeScrapper;
import com.poprosturonin.utils.ParsingUtils;
import com.poprosturonin.utils.URLUtils;
import org.json.JSONArray;
import org.json.JSONObject;
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
public class JbzdSingleMemeScrapper implements SingleMemeScrapper {
    private static final String JBZD_COMMENTS_URL = "https://jbzdy.pl/comments/listing";

    @Override
    public Optional<Meme> parseMeme(Document document) {
        Element memeElement = document.getElementsByTag("article").first();
        if (memeElement == null)
            throw new CouldNotParseMemeException();

        String title, url;
        int votes = 0;
        Element titleElement = memeElement.select("div.title > a").first();
        if (titleElement != null) {
            title = titleElement.text();
            url = titleElement.attr("href");
        } else
            throw new CouldNotParseMemeException();

        // Get votes
        Element plusOneElement = memeElement.select("a.btn-plus").first();
        if (plusOneElement != null) {
            votes = ParsingUtils.parseIntOrGetZero(plusOneElement.select("span").text());
        }

        // Get content
        Content content = getContent(memeElement.select("div.media").first());
        if (content == null)
            throw new CouldNotParseMemeException();

        // Get comments
        List<Comment> comments = getComments(document);

        // Get amount of comments
        int responses = 0;
        for (Comment comment : comments) {
            responses += comment.getResponses().size();
        }

        // Get tags
        List<Tag> tags = getTags(memeElement);

        // Get author
        Element authorElement = memeElement.select("div.info > a").first();
        Author author = null;
        if (authorElement != null)
             author = new Author(authorElement.text(), authorElement.attr("href"));

        Meme meme = new Meme();
        meme.setTitle(title);
        meme.setUrl(url);
        meme.setCommentAmount(responses + comments.size());
        meme.setPoints(votes);
        meme.setContent(content);
        meme.setComments(comments);
        meme.setAuthor(author);
        meme.setTags(tags);

        URLUtils.getPathId(url).ifPresent(s -> meme.setViewUrl(String.format("/jbzd/%s", s)));

        return Optional.of(meme);
    }

    private List<Comment> getComments(Document document) {
        Element commentSection = document.select("section[role=\"comments\"]").first();
        if (commentSection == null)
            return new ArrayList<>();

        List<JBZDComment> comments = new ArrayList<>(20);

        JSONObject responseJSON = new JSONObject(getCommentJSON(commentSection.attr("data-parent-id")));
        JSONArray commentsJSON = responseJSON.getJSONArray("comments");

        for (int i = 0; i < commentsJSON.length(); i++) {
            JSONObject commentJSON = (JSONObject) commentsJSON.get(i);

            Author author = new Author(commentJSON.getString("username"), commentJSON.getString("profile_url"));
            int points = commentJSON.getInt("score");
            String content = commentJSON.getString("comment");
            int id = commentJSON.getInt("id");

            JBZDComment newComment = new JBZDComment(content, author, points);
            newComment.setId(id);

            if (commentJSON.has("parent_comment_id") && !commentJSON.isNull("parent_comment_id")) {
                int parent = commentJSON.getInt("parent_comment_id");

                for (JBZDComment comment : comments) {
                    if (comment.getId() == parent) {
                        newComment.setReply(true);
                        comment.getResponses().add(newComment);
                        break;
                    }
                }
            }

            if (!newComment.isReply()) {
                comments.add(newComment);
            }
        }

        return comments.stream().map(jbzdComment -> (Comment) jbzdComment).collect(Collectors.toList());
    }

    private String getCommentJSON(String id) {
        try {
            String data = "parent_id=" + id; //fe. parent_id=608359
            String type = "application/x-www-form-urlencoded";
            URL downloadURL = new URL(JBZD_COMMENTS_URL);
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

    private List<Tag> getTags(Element memeElement) {
        Element tagListElement = memeElement.select("div.info").first();
        Elements tagsElements = tagListElement.select("a.tag");
        return tagsElements.stream()
                .map((Element e) -> new Tag(
                        e.text().replaceFirst("#", ""),
                        e.attr("href"),
                        URLUtils.cutToSecondSlash(e.attr("href")).orElse(" ").substring(1)))
                .collect(Collectors.toList());
    }

    private Content getContent(Element mediaElement) {
        Elements images = mediaElement.select("img");
        if (images.size() > 0) {
            if (images.attr("src").endsWith(".gif"))
                return new GIFContent(images.attr("src"));
            else
                return new ImageContent(images.attr("src"));
        }

        Elements videos = mediaElement.select("video > source");
        if (videos.size() > 0)
            return new VideoContent(videos.attr("src"));

        return null;
    }

    private class JBZDComment extends Comment {
        private int id;

        public JBZDComment(String content, Author author, int points) {
            super(content, author, points);
        }

        public JBZDComment(String content, Author author, List<Comment> responses, int points) {
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
