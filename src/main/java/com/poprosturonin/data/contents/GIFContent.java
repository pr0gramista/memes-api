package com.poprosturonin.data.contents;

/**
 * Single GIF animation
 */
public class GIFContent extends ImageContent {
    public GIFContent(String url) {
        super(url);
    }

    @Override
    public ContentType getContentType() {
        return ContentType.GIF;
    }
}
