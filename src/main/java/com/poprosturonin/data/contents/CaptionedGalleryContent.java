package com.poprosturonin.data.contents;

import java.util.List;

/**
 * Gallery with each image (it can be also a GIF) url and optional title and caption.
 */
public class CaptionedGalleryContent extends Content {
    private List<CaptionedGallerySlide> images;

    public CaptionedGalleryContent(List<CaptionedGallerySlide> images) {
        this.images = images;
    }

    public List<CaptionedGallerySlide> getImages() {
        return images;
    }

    public void setImages(List<CaptionedGallerySlide> images) {
        this.images = images;
    }

    @Override
    public ContentType getContentType() {
        return ContentType.CAPTIONED_GALLERY;
    }

    public static class CaptionedGallerySlide {
        private String url = "";
        private String title = "";
        private String caption = "";

        public CaptionedGallerySlide(String url, String title, String caption) {
            this.url = url;
            this.title = title;
            this.caption = caption;
        }

        public CaptionedGallerySlide(String url) {
            this.url = url;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getCaption() {
            return caption;
        }

        public void setCaption(String caption) {
            this.caption = caption;
        }
    }
}
