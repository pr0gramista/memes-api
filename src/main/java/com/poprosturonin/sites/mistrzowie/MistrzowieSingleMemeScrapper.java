package com.poprosturonin.sites.mistrzowie;

import com.poprosturonin.data.Author;
import com.poprosturonin.data.Comment;
import com.poprosturonin.data.Meme;
import com.poprosturonin.data.contents.Content;
import com.poprosturonin.data.contents.ImageContent;
import com.poprosturonin.exceptions.CouldNotParseMemeException;
import com.poprosturonin.sites.SingleMemeScrapper;
import com.poprosturonin.utils.ParsingUtils;
import com.poprosturonin.utils.URLUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class MistrzowieSingleMemeScrapper implements SingleMemeScrapper {
    private static final String ABS_URL = "http://mistrzowie.org";
    private static final String USER_URL = ABS_URL + "/user/";

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
        meme.setComments(getComments(meme.getViewUrl()));

        return Optional.of(meme);
    }

    private List<Comment> getComments(String viewUrl) {
        String id = viewUrl.replace("/mistrzowie/", "");
        Document commentsSnippet;
        try {
            commentsSnippet = Jsoup.parse(new URL(String.format("http://mistrzowie.org/pic/komentarze/%s", id)), 2000);
        } catch (IOException e) {
            return null;
        }

        List<Comment> comments = new ArrayList<>(20);
        Elements commentsElements = commentsSnippet.getElementsByClass("comment-box");
        for (Element commentElement : commentsElements) {
            Element userLink = commentElement.select(".username a").first();
            String nick = userLink.text().trim();
            Author author = new Author(nick, ABS_URL + userLink.attr("href"));

            String content = commentElement.select("p.commcontent").text().trim();

            Comment comment = new Comment(content, author, 0);
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
        Element canonical = document.select("link[rel=\"canonical\"]").first();
        if (canonical != null) {
            return canonical.attr("href");
        } else {
            return document.baseUri();
        }
    }

    private String getViewURL(String url) {
        String s = URLUtils.cutToFirstSlash(url).orElse(null);
        if (s != null)
            return String.format("/mistrzowie%s", s);
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
        return ParsingUtils.parseIntOrGetZero(mistrz.select(".count .value").text());
    }

    private ImageContent parseAsImage(Element mistrz) {
        return new ImageContent(ABS_URL + URLUtils.cutOffParameters(mistrz.select("img.pic").attr("src")));
    }
}
