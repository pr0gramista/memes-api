package com.poprosturonin.sites.ninegag;

import com.poprosturonin.data.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * The coding love
 */
@RestController
@RequestMapping(value = "/9gag")
public class NinegagController {
    final static String ROOT_URL = "http://9gag.com";
    final static String NINE_GAG_COUNT_PARAMETER = "/?c=";

    @Autowired
    private NinegagScrapper ninegagScrapper;

    @RequestMapping(value = "")
    @ResponseBody
    public Page readerPage() {
        return ninegagScrapper.scrap(ROOT_URL);
    }

    @RequestMapping(value = "/{id}")
    @ResponseBody
    public Page readerPage(@PathVariable int id) {
        id *= 10; //10 memes per page
        return ninegagScrapper.scrap(ROOT_URL + NINE_GAG_COUNT_PARAMETER + Integer.toString(id));
    }
}
