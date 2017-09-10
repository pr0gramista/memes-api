package com.poprosturonin.data;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.poprosturonin.data.contents.Content;

/**
 * Author of the meme
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Author {
    private String name;
    private String profileUrl;

    public Author(String name, String profileUrl) {
        this.name = name;
        this.profileUrl = profileUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }
}
