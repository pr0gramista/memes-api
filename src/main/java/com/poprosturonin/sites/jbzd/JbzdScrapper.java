package com.poprosturonin.sites.jbzd;

import com.poprosturonin.data.Meme;
import com.poprosturonin.data.Page;
import com.poprosturonin.data.contents.Content;
import com.poprosturonin.data.contents.GIFContent;
import com.poprosturonin.data.contents.ImageContent;
import com.poprosturonin.data.contents.VideoContent;
import com.poprosturonin.exceptions.PageIsEmptyException;
import com.poprosturonin.sites.Scrapper;
import com.poprosturonin.utils.URLUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class JbzdScrapper implements Scrapper {
    private final static String SEQUENCE_404 = "Error 404";

    private boolean is404(String title) {
        return title.contains(SEQUENCE_404);
    }

    @Override
    public Page parsePage(Document document) {
        Page page = new Page();

        String title = document.title();
        if (is404(title))
            throw new PageIsEmptyException();
        page.setTitle(title);

        //Get next link page
        Elements nextPageElement = document.getElementsByClass("btn-next-page");
        if (nextPageElement.size() > 0)
            page.setNextPage("/jbzd" + URLUtils.cutToSecondSlash(URLUtils.cutOffParameters(nextPageElement.get(0).attr("href"))).get());

        //Get content
        Elements listElements = document.select("section[role=listing] > article");
        List<Meme> memes = listElements.stream()
                .map(this::parseListElement)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());

        page.getMemes().addAll(memes);

        if (page.isEmpty())
            throw new PageIsEmptyException();

        return page;
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

    private Optional<Meme> parseListElement(Element element) {
        String title = null;
        String url = null;
        int comments = 0;
        int votes = 0;

        //Get header
        Element titleElement = element.select("div.title > a").first();
        if (titleElement != null) {
            title = titleElement.text();
            url = titleElement.attr("href");
        } else
            return Optional.empty();

        //Get votes
        Element plusOneElement = element.select("a.btn-plus").first();
        if (plusOneElement != null) {
            votes = Integer.parseInt(plusOneElement.select("span").text());
        }

        Content content = getContent(element.select("div.media").first());
        if (content == null)
            return Optional.empty();

        return Optional.of(new Meme(title, content, url, comments, votes));
    }
}
