package com.poprosturonin.sites.thecodinglove;

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
@RequestMapping(value = "/thecodinglove")
public class TheCodingLoveController {
    final static String ROOT_URL = "http://thecodinglove.com";
    final static String PAGE_URL = "/page/";

    @Autowired
    private TheCodingLoveScrapper theCodingLoveScrapper;

    @RequestMapping(value = "")
    @ResponseBody
    public Page readerPage() {
        return theCodingLoveScrapper.scrap(ROOT_URL);
    }

    @RequestMapping(value = "/{id}")
    @ResponseBody
    public Page readerPage(@PathVariable int id) {
        return theCodingLoveScrapper.scrap(ROOT_URL + PAGE_URL + Integer.toString(id));
    }
}
