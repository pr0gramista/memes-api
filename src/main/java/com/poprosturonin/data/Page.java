package com.poprosturonin.data;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.List;

/**
 * Single page of supported sites
 */
public class Page {
    /**
     * Title of the page
     */
    private String title;

    private List<Meme> memes = new ArrayList<>();

    /**
     * URL to next page
     */
    private String nextPage;

    @JsonIgnore
    public boolean isEmpty() {
        return memes.isEmpty();
    }

    /**
     * Gets title of this page
     *
     * @return title of this page
     */
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Meme> getMemes() {
        return memes;
    }

    /**
     * URL to next page relative to this server fe. /site/
     *
     * @return url to next page
     */
    public String getNextPage() {
        return nextPage;
    }

    public void setNextPage(String nextPage) {
        this.nextPage = nextPage;
    }
}
