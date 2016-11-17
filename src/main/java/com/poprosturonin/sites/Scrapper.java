package com.poprosturonin.sites;

import com.poprosturonin.data.Page;
import com.poprosturonin.exceptions.PageIsEmptyException;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

/**
 * Scrapper is being used to retrieve information
 * from supported sites.
 */
public interface Scrapper {
    /**
     * Scraps the website accessible from given URL.
     * Executes {@link #parse(Document)}
     *
     * @param url given URL
     * @return parsed page if possible, otherwise empty page
     */
    default Page scrap(String url) {
        try {
            return parse(Jsoup.connect(url).get());
        } catch (HttpStatusException e) {
            throw new PageIsEmptyException();
        } catch (IOException e) {
            e.printStackTrace();
        }
        throw new PageIsEmptyException();
    }

    /**
     * Parses given document
     *
     * @param document given documnet
     * @return parsed page if possible, otherwise empty page
     */
    Page parse(Document document);
}
