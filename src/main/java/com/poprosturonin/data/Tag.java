package com.poprosturonin.data;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Tag
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Tag {
    private String name;

    private String sourceUrl;

    private String slug;

    public Tag(String name, String sourceUrl, String slug) {
        this.name = name;
        this.sourceUrl = sourceUrl;
        this.slug = slug;
    }

    public Tag(String name, String sourceUrl) {
        this.name = name;
        this.sourceUrl = sourceUrl;
    }

    public Tag(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSourceUrl() {
        return sourceUrl;
    }

    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }
}
