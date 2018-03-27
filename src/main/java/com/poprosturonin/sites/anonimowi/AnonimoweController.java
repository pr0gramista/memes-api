package com.poprosturonin.sites.anonimowi;

import com.poprosturonin.data.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Anonimowe
 */
@RestController
@RequestMapping(value = "/anonimowe")
public class AnonimoweController {
    final static String ROOT_URL = "https://anonimowe.pl";

    @Autowired
    private AnonimowePageScrapper anonimowePageScrapper;

    @RequestMapping(value = "")
    @ResponseBody
    public Page readerPage() {
        return anonimowePageScrapper.scrapPage(ROOT_URL);
    }

    @RequestMapping(value = "/page/{id}")
    @ResponseBody
    public Page readerPage(@PathVariable int id) {
        return anonimowePageScrapper.scrapPage(ROOT_URL + '/' + Integer.toString(id));
    }
}
