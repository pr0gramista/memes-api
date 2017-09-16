package com.poprosturonin.sites.jbzd;

import com.poprosturonin.data.Meme;
import com.poprosturonin.data.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Jbzd
 */
@RestController
@RequestMapping(value = "/jbzd")
public class JbzdController {
    final static String JBZD_ROOT_URL = "http://jbzd.pl";
    final static String JBZD_PAGE_URL = "/strona/";

    @Autowired
    private JbzdPageScrapper jbzdPageScrapper;

    @Autowired
    private JbzdSingleMemeScrapper jbzdSingleMemeScrapper;

    @RequestMapping(value = "")
    @ResponseBody
    public Page readerPage() {
        return jbzdPageScrapper.scrapPage(JBZD_ROOT_URL);
    }

    @RequestMapping(value = "/page/{id}")
    @ResponseBody
    public Page readerPage(@PathVariable int id) {
        return jbzdPageScrapper.scrapPage(JBZD_ROOT_URL + JBZD_PAGE_URL + Integer.toString(id));
    }

    @RequestMapping(value = "/{id}")
    @ResponseBody
    public Meme readerMeme(@PathVariable int id) {
        return jbzdSingleMemeScrapper.scrapMeme(JBZD_ROOT_URL + "/obr/" + Integer.toString(id));
    }
}
