package com.poprosturonin.data.contents;

/**
 * Content can be a text, image, video or a gallery
 * it does hold only content, no metadata included
 */
public abstract class Content {
    public abstract ContentType getContentType();
}
