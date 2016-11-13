package com.poprosturonin.data.contents;

/**
 * Single video
 */
public class VideoContent extends Content {
    private String url;

    public VideoContent(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public ContentType getContentType() {
        return ContentType.VIDEO;
    }
}
