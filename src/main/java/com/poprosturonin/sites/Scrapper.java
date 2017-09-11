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
     * Because some sites apparently do not like bots, we need to pretend... to be most popular web browser
     */
    String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.99 Safari/537.36";

    /**
     * Scraps the page of memes accessible from given URL.
     * Executes {@link #parsePage(Document)}
     *
     * @param url given URL
     * @return parsed page if possible, otherwise empty page
     */
    default Page scrapPage(String url) {
        try {
            return parsePage(Jsoup.connect(url).userAgent(USER_AGENT).get());
        } catch (HttpStatusException e) {
            e.printStackTrace();
            throw new PageIsEmptyException();
        } catch (IOException e) {
            e.printStackTrace();
        }
        throw new PageIsEmptyException();
    }

    /**
     * Parses given document as a page
     *
     * @param document given document
     * @return parsed page if possible, otherwise empty page
     */
    Page parsePage(Document document);
}
