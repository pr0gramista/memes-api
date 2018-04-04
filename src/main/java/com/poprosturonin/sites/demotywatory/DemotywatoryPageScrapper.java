package com.poprosturonin.sites.demotywatory;

import com.poprosturonin.data.Meme;
import com.poprosturonin.data.Page;
import com.poprosturonin.data.contents.*;
import com.poprosturonin.exceptions.PageIsEmptyException;
import com.poprosturonin.sites.PageScrapper;
import com.poprosturonin.utils.ParsingUtils;
import com.poprosturonin.utils.URLUtils;
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

import static com.poprosturonin.sites.demotywatory.DemotywatoryController.ROOT_URL;

/**
 * Demotywatory scrapper
 */
@Component
public class DemotywatoryPageScrapper implements PageScrapper {

    DemotywatoryGalleryParser galleryParser = new DemotywatoryGalleryParser();

    private Optional<Meme> parsePicture(Element demot) {
        if (demot.hasClass("image"))
            return parseAsImage(demot);
        else if (demot.hasClass("video_mp4"))
            return parseAsVideo(demot);
        else if (demot.hasClass("image_gif"))
            return parseAsGIF(demot);
        else if (demot.hasClass("image_gallery"))
            return parseAsGallery(demot);

        return Optional.empty();
    }

    private Optional<Meme> parseAsGIF(Element demot) {
        String title;
        String description;
        String url;
        Content content;

        // Get content
        Elements contentElement = demot.getElementsByTag("img");
        content = new GIFContent(contentElement.attr("src"));

        // Get title
        title = demot.getElementsByClass("demot_title").text();

        // Get description
        description = demot.getElementsByClass("demot_description").text();

        // Get url
        Elements urlElements = demot.getElementsByClass("demot_link");
        url = ROOT_URL + urlElements.attr("href");

        return Optional.of(new Meme(title, content, url, description, getComments(demot), getVotes(demot)));
    }

    private Optional<Meme> parseAsImage(Element demot) {
        String title, url;
        Content content;

        // Get content
        Elements contentElement = demot.getElementsByTag("img");
        content = new ImageContent(contentElement.attr("src"));

        // Get title
        title = contentElement.attr("alt");

        // Get url
        Elements urlElements = demot.getElementsByTag("a");
        url = ROOT_URL + urlElements.attr("href");

        return Optional.of(new Meme(title, content, url, getComments(demot), getVotes(demot)));
    }

    private Optional<Meme> parseAsVideo(Element demot) {
        String title, description, url;
        Content content;

        //Get content
        Elements contentElement = demot.getElementsByTag("source");
        content = new VideoContent(ROOT_URL + contentElement.attr("src"));

        // Get title
        title = demot.getElementsByClass("demot_title").text();

        // Get description
        description = demot.getElementsByClass("demot_description").text();

        // Get url
        Elements urlElements = demot.getElementsByClass("demot_link");
        url = ROOT_URL + urlElements.attr("href");

        return Optional.of(new Meme(title, content, url, description, getComments(demot), getVotes(demot)));
    }

    private Optional<Meme> parseAsGallery(Element demot) {
        Elements galleryThumbnail = demot.getElementsByTag("img");
        String title = galleryThumbnail.attr("alt");
        String url = ROOT_URL + demot.getElementsByTag("a").attr("href");
        List<CaptionedGalleryContent.CaptionedGallerySlide> singles = new ArrayList<>();

        // Put gallery thumbnail as first image
        singles.add(new CaptionedGalleryContent.CaptionedGallerySlide(
                galleryThumbnail.attr("src")
        ));

        // Put slides
        CaptionedGalleryContent galleryContent = new CaptionedGalleryContent(singles);
        try {
            galleryContent.getImages().addAll(galleryParser.parse(Jsoup.connect(url).userAgent(USER_AGENT).get()));
        } catch (IOException e) {
            e.printStackTrace();
            return Optional.empty();
        }

        return Optional.of(new Meme(title, galleryContent, url, getComments(demot), getVotes(demot)));
    }

    private int getComments(Element demot) {
        return ParsingUtils.parseIntOrGetZero(demot.getElementsByClass("demot-comments").select("a").text());
    }

    private int getVotes(Element demot) {
        return ParsingUtils.parseIntOrGetZero(demot.getElementsByClass("up_votes").text());
    }

    public Page parsePage(Document document) {
        Page page = new Page();

        // Get next link page
        Elements nextPageElement = document.getElementsByClass("next-page");
        if (nextPageElement.size() > 0)
            page.setNextPage("/demotywatory/page" + URLUtils.cutToSecondSlash(URLUtils.cutOffParameters(nextPageElement.get(0).attr("href"))).get());

        // Get content
        Elements pictures = document.getElementsByClass("demot_pic");
        List<Meme> memes = pictures.stream()
                .map(this::parsePicture)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .peek(meme -> URLUtils.getPathId(meme.getUrl()).ifPresent(s -> meme.setViewUrl(String.format("/demotywatory/%s", s))))
                .collect(Collectors.toList());

        page.getMemes().addAll(memes);

        page.setTitle(document.title());

        if (page.isEmpty())
            throw new PageIsEmptyException();

        return page;
    }
}
