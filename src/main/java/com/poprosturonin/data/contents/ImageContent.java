package com.poprosturonin.data.contents;

/**
 * Single image
 */
public class ImageContent extends Content {
    private String url;

    public ImageContent(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public ContentType getContentType() {
        return ContentType.IMAGE;
    }
}
