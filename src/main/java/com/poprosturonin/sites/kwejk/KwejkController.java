package com.poprosturonin.sites.kwejk;

import com.poprosturonin.data.Meme;
import com.poprosturonin.data.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Kwejk
 */
@RestController
@RequestMapping(value = "/kwejk")
public class KwejkController {
    final static String KWEJK_ROOT_URL = "http://kwejk.pl";
    final static String KWEJK_PAGE_URL = "/strona/";
    final static String KWEJK_SINGLE_URL = "/obrazek/";

    @Autowired
    private KwejkPageScrapper kwejkPageScrapper;

    @Autowired
    private KwejkSingleMemeScrapper kwejkSingleMemeScrapper;

    @RequestMapping(value = "")
    @ResponseBody
    public Page readerPage() {
        return kwejkPageScrapper.scrapPage(KWEJK_ROOT_URL);
    }

    @RequestMapping(value = "/page/{id}")
    @ResponseBody
    public Page readerPage(@PathVariable int id) {
        return kwejkPageScrapper.scrapPage(KWEJK_ROOT_URL + KWEJK_PAGE_URL + Integer.toString(id));
    }

    @RequestMapping(value = "/{id}")
    @ResponseBody
    public Meme readerMeme(@PathVariable int id) {
        return kwejkSingleMemeScrapper.scrapMeme(KWEJK_ROOT_URL + KWEJK_SINGLE_URL + Integer.toString(id));
    }
}
