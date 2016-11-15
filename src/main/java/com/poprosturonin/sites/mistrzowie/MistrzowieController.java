package com.poprosturonin.sites.mistrzowie;

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
    private MistrzowieScrapper mistrzowieScrapper;

    @RequestMapping(value = "")
    @ResponseBody
    public Page readerPage() {
        return mistrzowieScrapper.scrap(ROOT_URL);
    }

    @RequestMapping(value = "/{id}")
    @ResponseBody
    public Page readerPage(@PathVariable int id) {
        return mistrzowieScrapper.scrap(ROOT_URL + PAGE_URL + Integer.toString(id));
    }
}
