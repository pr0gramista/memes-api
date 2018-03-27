package com.poprosturonin.data.contents;

/**
 * Single image
 */
public class TextContent extends Content {
    private String content;

    public TextContent(String content) {
        this.content = content;
    }

    public String getUrl() {
        return content;
    }

    @Override
    public ContentType getContentType() {
        return ContentType.TEXT;
    }
}
