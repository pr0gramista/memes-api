package com.poprosturonin.sites.ninegag;

import com.poprosturonin.data.Page;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

/**
 * Mistrzowie scrapper
 */
@Component
public class NinegagNSFWPageScrapper extends NinegagPageScrapper {
    @Override
    public Page parseJSONPage(JSONObject response) {
        Page page = super.parseJSONPage(response);
        page.setNextPage(page.getNextPage().replace("9gag", "9gagnsfw"));
        return page;
    }
}
