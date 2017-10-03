package com.poprosturonin.sites.mistrzowie;

import com.poprosturonin.data.Meme;
import com.poprosturonin.data.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Mistrzowie
 */
@RestController
@RequestMapping(value = "/mistrzowie")
public class MistrzowieController {
    final static String ROOT_URL = "http://mistrzowie.org";
    final static String PAGE_URL = "/page/";

    @Autowired
    private MistrzowiePageScrapper mistrzowiePageScrapper;

    @Autowired
    private MistrzowieSingleMemeScrapper mistrzowieSingleMemeScrapper;

    @RequestMapping(value = "")
    @ResponseBody
    public Page readerPage() {
        return mistrzowiePageScrapper.scrapPage(ROOT_URL);
    }

    @RequestMapping(value = "/page/{id}")
    @ResponseBody
    public Page readerPage(@PathVariable int id) {
        return mistrzowiePageScrapper.scrapPage(ROOT_URL + PAGE_URL + Integer.toString(id));
    }

    @RequestMapping(value = "/{id}")
    @ResponseBody
    public Meme readerMeme(@PathVariable int id) {
        return mistrzowieSingleMemeScrapper.scrapMeme(ROOT_URL + "/" + Integer.toString(id));
    }
}
