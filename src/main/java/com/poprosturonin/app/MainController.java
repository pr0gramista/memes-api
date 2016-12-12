package com.poprosturonin.app;

import com.poprosturonin.utils.SiteDiscovery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * Main controller lists all available sites API.
 */
@RestController
public class MainController {
    private List<String> urls;

    @Autowired
    private SiteDiscovery siteDiscovery;

    @RequestMapping(path = "/")
    @ResponseBody
    public List<String> sites() {
        return urls;
    }

    @EventListener({ContextRefreshedEvent.class})
    void contextRefreshedEvent() {
        try {
            urls = siteDiscovery.getAllSitesUrls();
        } catch (ClassNotFoundException e) {
            urls = new ArrayList<>();
        }
    }
}
