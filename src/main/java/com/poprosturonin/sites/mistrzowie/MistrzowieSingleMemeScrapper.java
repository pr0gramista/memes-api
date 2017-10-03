package com.poprosturonin.sites.mistrzowie;

import com.poprosturonin.data.Author;
import com.poprosturonin.data.Comment;
import com.poprosturonin.data.Meme;
import com.poprosturonin.data.contents.Content;
import com.poprosturonin.data.contents.ImageContent;
import com.poprosturonin.exceptions.CouldNotParseMemeException;
import com.poprosturonin.sites.SingleMemeScrapper;
import com.poprosturonin.utils.URLUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class MistrzowieSingleMemeScrapper implements SingleMemeScrapper {
    private static final String USER_URL = "http://mistrzowie.org/user/";

    @Override
    public Optional<Meme> parseMeme(Document document) {
        Meme meme = new Meme();

        Element mistrz = document.select("#main_container div.pic").first();
        if (mistrz == null)
            throw new CouldNotParseMemeException();

        meme.setAuthor(getAuthor(mistrz));
        meme.setContent(getContent(mistrz));
        meme.setTitle(getTitle(mistrz));
        meme.setPoints(getVotes(mistrz));

        meme.setUrl(getURL(document));
        meme.setViewUrl(getViewURL(meme.getUrl()));
        meme.setComments(getComments(document));

        return Optional.of(meme);
    }

    private List<Comment> getComments(Element document) {
        List<Comment> comments = new ArrayList<>(20);
        Element commentsElement = document.select("#comments").first();

        Elements commentElements = commentsElement.select(".comment");
        for (Element commentElement : commentElements) {
            //Comment comment = new Comment()

            String nick = commentElement.select(".username a").first().text().trim();
            Author author = new Author(nick, USER_URL + nick);
            String content = commentElement.select("p.commcontent").text().trim();

            int points = Integer.parseInt(commentElement.select("span.points").text());

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
        Element nickElement = demot.select(".pic_username").first();
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
            return String.format("/mistrzowie/%s", s);
        else
            return null;
    }

    private String getTitle(Element mistrz) {
        return mistrz.select("h1.picture").text().trim();
    }

    private Content getContent(Element mistrz) {
        return parseAsImage(mistrz);
    }

    private int getVotes(Element mistrz) {
        int votes;
        try {
            votes = Integer.parseInt(mistrz.getElementsByClass("total_votes_up > span.value").text());
        } catch (NumberFormatException exception) {
            return 0;
        }
        return votes;
    }

    private ImageContent parseAsImage(Element mistrz) {
        return new ImageContent(mistrz.select("img.pic").attr("src"));
    }
}
