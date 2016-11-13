package com.poprosturonin.sites.kwejk;

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

    @Autowired
    private KwejkScrapper kwejkScrapper;

    @RequestMapping(value = "/")
    @ResponseBody
    public Page readerPage() {
        return kwejkScrapper.scrap(KWEJK_ROOT_URL);
    }

    @RequestMapping(value = "/{id}")
    @ResponseBody
    public Page readerPage(@PathVariable int id) {
        return kwejkScrapper.scrap(KWEJK_ROOT_URL + KWEJK_PAGE_URL + Integer.toString(id));
    }
}
