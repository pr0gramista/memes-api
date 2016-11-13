package com.poprosturonin.sites;

import com.poprosturonin.data.Page;
import org.jsoup.nodes.Document;

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
    Page scrap(String url);

    /**
     * Parses given document
     *
     * @param document given documnet
     * @return parsed page if possible, otherwise empty page
     */
    Page parse(Document document);
}
