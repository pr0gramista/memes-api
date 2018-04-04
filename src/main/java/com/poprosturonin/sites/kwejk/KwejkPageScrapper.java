package com.poprosturonin.sites.kwejk;

import com.poprosturonin.data.Author;
import com.poprosturonin.data.Meme;
import com.poprosturonin.data.Page;
import com.poprosturonin.data.Tag;
import com.poprosturonin.data.contents.GalleryContent;
import com.poprosturonin.data.contents.ImageContent;
import com.poprosturonin.data.contents.VideoContent;
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

/**
 * Kwejk scrapper
 */
@Component
public class KwejkPageScrapper implements PageScrapper {
    private final static String SEQUENCE_404 = "404 - strona";

    private boolean is404(String title) {
        return title.contains(SEQUENCE_404);
    }

    private List<String> parseGallery(String url) {
        List<String> list = new ArrayList<>(14);
        try {
            Document document = Jsoup.connect(url).userAgent(USER_AGENT).get();
            Elements thumbnails = document.select(".slider-nav > li > a > img");
            thumbnails.forEach((Element thumbnail) -> list.add(thumbnail.attr("src").replace("_thumb", "")));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    private Optional<Meme> parseMemeBlock(Element block) {
        String title, url;
        int comments, votes;

        // Get title
        Element titleElement = block.select(".content > h2 > a").first();
        if (titleElement == null) {
            // If no header was found, skip this article
            return Optional.empty();
        }

        title = titleElement.text();
        url = titleElement.attr("href");

        // Get author
        Author author = null;
        Element authorElement = block.select("div.user-bar > div.content > a").first();
        if (authorElement != null) {
            author = new Author(
                    authorElement.getElementsByClass("name").first().text(),
                    authorElement.attr("href"));
        }

        // Get comments and votes
        comments = ParsingUtils.parseIntOrGetZero(block.attr("data-comments-count"));
        votes = ParsingUtils.parseIntOrGetZero(block.attr("data-vote-up"))
                - ParsingUtils.parseIntOrGetZero(block.attr("data-vote-down"));

        // Get tags
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

        Optional<GalleryContent> galleryContent = tryToParseAsGalleryContent(url);
        if (galleryContent.isPresent()) {
            Meme meme = new Meme(title, galleryContent.get(), url, comments, votes);
            meme.setAuthor(author);
            meme.setTags(tags);
            return Optional.of(meme);
        }

        Optional<VideoContent> videoContent = tryToParseAsVideoContent(block);
        if (videoContent.isPresent()) {
            Meme meme = new Meme(title, videoContent.get(), url, comments, votes);
            meme.setAuthor(author);
            meme.setTags(tags);
            return Optional.of(meme);
        }

        Optional<ImageContent> imageContent = tryToParseAsImageContent(block);
        if (imageContent.isPresent()) {
            Meme meme = new Meme(title, imageContent.get(), url, comments, votes);
            meme.setAuthor(author);
            meme.setTags(tags);
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

    private Optional<GalleryContent> tryToParseAsGalleryContent(String headerURL) {
        if (headerURL.contains("/przegladaj/")) {
            return Optional.of(new GalleryContent(parseGallery(headerURL)));
        } else
            return Optional.empty();
    }

    public Page parsePage(Document document) {
        Page page = new Page();

        String title = document.title();
        if (is404(title))
            throw new PageIsEmptyException();
        page.setTitle(document.title());

        //Get next link page
        Elements nextPageElement = document.getElementsByClass("btn-next");
        if (nextPageElement.size() > 0) {
            page.setNextPage("/kwejk/page" + URLUtils.cutToSecondSlash(URLUtils.cutOffParameters(nextPageElement.get(0).attr("href"))).get());
        }

        //Get content
        Elements memeBlocks = document.select(".media-element");
        List<Meme> memes = memeBlocks.stream()
                .map(this::parseMemeBlock)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .peek(meme -> URLUtils.getPathId(meme.getUrl()).ifPresent(s -> meme.setViewUrl(String.format("/kwejk/%s", s))))
                .collect(Collectors.toList());

        page.getMemes().addAll(memes);

        if (page.isEmpty())
            throw new PageIsEmptyException();

        return page;
    }
}
