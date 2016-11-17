package com.poprosturonin.sites.kwejk;

import com.poprosturonin.data.Meme;
import com.poprosturonin.data.Page;
import com.poprosturonin.data.contents.GalleryContent;
import com.poprosturonin.data.contents.ImageContent;
import com.poprosturonin.data.contents.VideoContent;
import com.poprosturonin.exceptions.PageIsEmptyException;
import com.poprosturonin.sites.Scrapper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Kwejk scrapper
 */
@Component
public class KwejkScrapper implements Scrapper {
    private final static String SEQUENCE_404 = "404 - strona";

    private boolean is404(String title) {
        return title.contains(SEQUENCE_404);
    }

    private List<String> parseGallery(String url) {
        List<String> list = new ArrayList<>();
        try {
            Document document = Jsoup.connect(url).get();
            Elements thumbnails = document.select(".jcarousel img");
            thumbnails.forEach((Element thumbnail) -> list.add(thumbnail.attr("src").replace("_thumb", "")));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    private Optional<Meme> parseArticle(Element article) {
        String title = null;
        String url = null;
        int comments = 0;
        int votes = 0;

        //Get header
        Elements headers = article.select("h1 > art-ah0 > a");
        if (headers.size() > 0) {
            Element headerElement = headers.get(0);
            title = headerElement.text();
            url = headerElement.attr("href");
        }

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

        Optional<GalleryContent> galleryContent = tryToParseAsGalleryContent(url);
        if (galleryContent.isPresent())
            return Optional.of(new Meme(title, galleryContent.get(), url, comments, votes));

        Optional<ImageContent> imageContent = tryToParseAsImageContent(article);
        if (imageContent.isPresent())
            return Optional.of(new Meme(title, imageContent.get(), url, comments, votes));

        Optional<VideoContent> videoContent = tryToParseAsVideoContent(article);
        if (videoContent.isPresent())
            return Optional.of(new Meme(title, videoContent.get(), url, comments, votes));

        return Optional.empty();
    }

    private Optional<ImageContent> tryToParseAsImageContent(Element article) {
        Elements images = article.select("img");
        if (images.size() > 0) {
            Element contentElement = images.get(0);
            return Optional.of(new ImageContent(contentElement.attr("src")));
        } else
            return Optional.empty();
    }

    private Optional<VideoContent> tryToParseAsVideoContent(Element article) {
        Elements videos = article.select("video");
        if (videos.size() > 0) {
            Element contentElement = videos.get(0);
            return Optional.of(new VideoContent(contentElement.attr("src")));
        } else
            return Optional.empty();
    }

    private Optional<GalleryContent> tryToParseAsGalleryContent(String headerURL) {
        if (headerURL.contains("/przegladaj/")) {
            return Optional.of(new GalleryContent(parseGallery(headerURL)));
        } else
            return Optional.empty();
    }

    public Page parse(Document document) {
        Page page = new Page();

        String title = document.title();
        if (is404(title))
            throw new PageIsEmptyException();
        page.setTitle(document.title());

        //Get next link page
        Elements nextPageElement = document.getElementsByClass("btn-next-page");
        if (nextPageElement.size() > 0)
            page.setNextPage(nextPageElement.get(0).attr("href").replace("http://kwejk.pl/strona", "/kwejk"));

        //Get content
        Elements articles = document.getElementsByTag("article");
        List<Meme> memes = articles.stream()
                .map(this::parseArticle)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());

        page.getMemes().addAll(memes);

        if (page.isEmpty())
            throw new PageIsEmptyException();

        return page;
    }
}
