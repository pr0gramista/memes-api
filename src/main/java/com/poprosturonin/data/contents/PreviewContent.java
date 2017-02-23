package com.poprosturonin.data.contents;

/**
 * Preview of long meme that need to accessed
 * by clicking on it
 */
public class PreviewContent extends ImageContent {
    public PreviewContent(String url) {
        super(url);
    }

    @Override
    public ContentType getContentType() {
        return ContentType.PREVIEW;
    }
}
