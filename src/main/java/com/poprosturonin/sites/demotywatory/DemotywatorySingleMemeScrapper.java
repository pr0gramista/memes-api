package com.poprosturonin.sites.demotywatory;

import com.poprosturonin.data.Author;
import com.poprosturonin.data.Comment;
import com.poprosturonin.data.Meme;
import com.poprosturonin.data.contents.*;
import com.poprosturonin.exceptions.CouldNotParseMemeException;
import com.poprosturonin.sites.SingleMemeScrapper;
import com.poprosturonin.utils.ParsingUtils;
import com.poprosturonin.utils.URLUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class DemotywatorySingleMemeScrapper implements SingleMemeScrapper {
    private static final String USER_URL = "http://demotywatory.pl/user/";
    private static final String ROOT_URL = "http://m.demotywatory.pl";

    private DemotywatoryGalleryParser galleryParser = new DemotywatoryGalleryParser();

    @Override
    public Optional<Meme> parseMeme(Document document) {
        Meme meme = new Meme();

        Element demot = document.getElementsByClass("demot_pic").first();
        if (demot == null)
            throw new CouldNotParseMemeException();

        meme.setAuthor(getAuthor(demot));
        meme.setContent(getContent(demot));
        meme.setTitle(getTitle(demot));
        meme.setCommentAmount(getCommentAmount(demot));
        meme.setPoints(getVotes(demot));

        meme.setUrl(getURL(document));
        meme.setViewUrl(getViewURL(meme.getUrl()));
        meme.setComments(getComments(document));

        return Optional.of(meme);
    }

    private List<Comment> getComments(Element document) {
        List<Comment> comments = new ArrayList<>(25);
        Element commentsElement = document.getElementById("comments");

        Elements commentElements = commentsElement.select(".comment");
        for (Element commentElement : commentElements) {
            String nick = commentElement.select("a.username").text().trim();
            Author author = new Author(nick, USER_URL + nick);
            String content = commentElement.select("p.comment-content").text().trim();

            int points = ParsingUtils.parseIntOrGetZero(commentElement.select("span.total_points").text());

            Comment comment = new Comment(content, author, points);
            if (commentElement.hasClass("reply")) {
                comment.setReply(true);
                comments.get(comments.size() - 1).getResponses().add(comment);
            } else {
                comments.add(comment);
            }
        }

        return comments;
    }

    private Author getAuthor(Element demot) {
        Element nickElement = demot.select(".demot-nick").first();
        if (nickElement != null) {
            String nick = nickElement.ownText().trim();

            return new Author(nick, USER_URL + nick);
        } else {
            return null;
        }
    }

    private String getURL(Document document) {
        return document.baseUri();
    }

    private String getViewURL(String url) {
        String s = URLUtils.cutToFirstSlash(url).orElse(null);
        if (s != null)
            return String.format("/demotywatory%s", s);
        else
            return null;
    }

    private String getTitle(Element demot) {
        if (demot.hasClass("image"))
            return demot.getElementsByTag("img").attr("alt").trim();
        else if (demot.hasClass("video_mp4"))
            return demot.getElementsByClass("demot_title").text().trim();
        else if (demot.hasClass("image_gif"))
            return demot.getElementsByTag("img").attr("alt").trim();
        else
            return "";
    }

    private Content getContent(Element demot) {
        if (demot.hasClass("image"))
            return parseAsImage(demot);
        else if (demot.hasClass("video_mp4"))
            return parseAsVideo(demot);
        else if (demot.hasClass("image_gif"))
            return parseAsGIF(demot);
        else if (demot.hasClass("image_gallery"))
            return parseAsGallery(demot);
        return parseAsImage(demot);
    }

    private CaptionedGalleryContent parseAsGallery(Element demot) {
        return new CaptionedGalleryContent(galleryParser.parse(demot));
    }

    private int getCommentAmount(Element demot) {
        return ParsingUtils.parseIntOrGetZero(demot.getElementsByClass("demot-comments").select("a").text());
    }

    private int getVotes(Element demot) {
        return ParsingUtils.parseIntOrGetZero(demot.getElementsByClass("up_votes").text());
    }

    private GIFContent parseAsGIF(Element demot) {
        return new GIFContent(demot.getElementsByTag("img").attr("src"));
    }

    private VideoContent parseAsVideo(Element demot) {
        return new VideoContent(ROOT_URL + demot.getElementsByTag("source").attr("src"));
    }

    private ImageContent parseAsImage(Element demot) {
        return new ImageContent(demot.getElementsByTag("img").attr("src"));
    }
}
