package com.poprosturonin.sites.demotywatory;

import com.poprosturonin.data.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Demotywatory
 */
@RestController
@RequestMapping(value = "/demotywatory")
public class DemotywatoryController {
    final static String ROOT_URL = "http://m.demotywatory.pl";
    final static String PAGE_URL = "/page/";

    @Autowired
    private DemotywatoryScrapper demotywatoryScrapper;

    @RequestMapping(value = "")
    @ResponseBody
    public Page readerPage() {
        return demotywatoryScrapper.scrapPage(ROOT_URL);
    }

    @RequestMapping(value = "/page/{id}")
    @ResponseBody
    public Page readerPage(@PathVariable int id) {
        return demotywatoryScrapper.scrapPage(ROOT_URL + PAGE_URL + Integer.toString(id));
    }
}
