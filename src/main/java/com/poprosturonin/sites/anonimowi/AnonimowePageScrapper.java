package com.poprosturonin.sites.anonimowi;

import com.poprosturonin.data.Meme;
import com.poprosturonin.data.Page;
import com.poprosturonin.data.contents.TextContent;
import com.poprosturonin.exceptions.PageIsEmptyException;
import com.poprosturonin.sites.PageScrapper;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Anonimowe scrapper
 */
@Component
public class AnonimowePageScrapper implements PageScrapper {
    private Optional<Meme> parseArticle(Element article) {
        Optional<TextContent> textContentOptional = Optional.ofNullable(article.select("section").first()).map(Element::text).map(TextContent::new);
        if (!textContentOptional.isPresent()) {
            return Optional.empty();
        }

        Optional<Element> header = Optional.ofNullable(article.select("header.story-header a").first());
        if (!header.isPresent()) {
            return Optional.empty();
        }

        String hash = header.get().text();
        String url = header.get().attr("href");
        int comments = 0;
        int points = getVotes(article);

        Meme meme = new Meme(hash, textContentOptional.get(), url, comments, points);
        return Optional.of(meme);
    }

    private int getVotes(Element article) {
        int votes;
        try {
            votes = Integer.parseInt(article.select("span.points").first().text());
        } catch (NumberFormatException | NullPointerException exception) {
            return 0;
        }
        return votes;
    }

    public Page parsePage(Document document) {
        Page page = new Page();

        page.setNextPage(Optional.ofNullable(document.select("nav.pagination > div.next > a").first())
                .map(element -> "/anonimowe/page" + element.attr("href"))
                .orElse(null));

        //Get content
        Elements pictures = document.select("article.story");
        List<Meme> memes = pictures.stream()
                .map(this::parseArticle)
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
