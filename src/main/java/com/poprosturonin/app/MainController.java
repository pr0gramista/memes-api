package com.poprosturonin.app;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Main controller represent our app identity.
 */
@Controller
public class MainController {
    @RequestMapping(path = "/")
    public String getIndex() {
        return "index";
    }
}
