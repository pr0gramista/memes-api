package com.poprosturonin.sites.ninegag;

import com.poprosturonin.data.Page;
import com.poprosturonin.sites.Scrapper;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

/**
 * Mistrzowie scrapper
 */
@Component
public class NinegagScrapper implements Scrapper {

    public Page parse(Document document) {
        return new Page();
    }
}
