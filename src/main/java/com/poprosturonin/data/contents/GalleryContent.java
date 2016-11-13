package com.poprosturonin.data.contents;

import java.util.List;

/**
 * Gallery with only urls
 */
public class GalleryContent extends Content {
    private List<String> urls;

    public GalleryContent(List<String> urls) {
        this.urls = urls;
    }

    public List<String> getUrls() {
        return urls;
    }

    @Override
    public ContentType getContentType() {
        return ContentType.GALLERY;
    }
}