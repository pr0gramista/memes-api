package com.poprosturonin.data;

import com.poprosturonin.data.contents.Content;

/**
 * Single meme with some content
 */
public class Meme {
    private String title;
    private Content content;
    private String url;
    private String description;
    private int comments;
    private int points;

    public Meme(String title, Content content, String url, int comments, int points) {
        this.title = title;
        this.content = content;
        this.url = url;
        this.comments = comments;
        this.points = points;
    }

    public Meme(String title, Content content, String url, String description, int comments, int points) {
        this.title = title;
        this.content = content;
        this.url = url;
        this.description = description;
        this.comments = comments;
        this.points = points;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Content getContent() {
        return content;
    }

    public void setContent(Content content) {
        this.content = content;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getComments() {
        return comments;
    }

    public void setComments(int comments) {
        this.comments = comments;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }
}
