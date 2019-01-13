package com.poprosturonin.sites.thecodinglove;

import com.poprosturonin.data.Meme;
import com.poprosturonin.data.Page;
import com.poprosturonin.data.contents.GIFContent;
import com.poprosturonin.data.contents.VideoContent;
import com.poprosturonin.exceptions.PageIsEmptyException;
import com.poprosturonin.sites.PageScrapper;
import com.poprosturonin.utils.URLUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * TheCodingLove scrapper
 */
@Component
public class TheCodingLovePageScrapper implements PageScrapper {

    private Optional<Meme> parsePicture(Element post) {
        Element titleAsLink = post.select(".blog-post-title > a").first();
        Optional<Element> image = Optional.ofNullable(post.select(".blog-post-content img").first());

        if (image.isPresent()) {
            return Optional.of(
                    new Meme(
                            titleAsLink.text(),
                            new GIFContent(image.get().attr("src")),
                            titleAsLink.attr("href"),
                            0,
                            0)
            );
        }

        Optional<Element> video = Optional.ofNullable(post.select(".blog-post-content video > source").first());

        return video.map(element -> new Meme(
                titleAsLink.text(),
                new VideoContent(element.attr("src")),
                titleAsLink.attr("href"),
                0,
                0));
    }

    public Page parsePage(Document document) {
        Page page = new Page();

        // Get next link page
        Element nextPageElement = document.select(".next-posts-btn").last();
        if (nextPageElement != null)
            page.setNextPage("/thecodinglove/page" + URLUtils.cutToSecondSlash(nextPageElement.attr("href")).get());

        // Get content
        Elements pictures = document.select(".blog-post");
        List<Meme> memes = pictures.stream()
                .map(this::parsePicture)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());

        page.getMemes().addAll(memes);

        page.setTitle(document.title());

        if (page.isEmpty())
            throw new PageIsEmptyException();

        return page;
    }
}
