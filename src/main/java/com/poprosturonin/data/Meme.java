package com.poprosturonin.data;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.poprosturonin.data.contents.Content;

import java.util.List;

/**
 * Single meme with some content
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Meme {
    private String title;
    private Content content;
    private String url;
    private String viewUrl;
    private Author author;
    private List<Comment> comments;

    private String description;
    private int commentAmount;
    private int points;

    public Meme() {
    }

    public Meme(String title, Content content, String url, int comments, int points) {
        this.title = title;
        this.content = content;
        this.url = url;
        this.commentAmount = comments;
        this.points = points;
    }

    public Meme(String title, Content content, String url, String description, int comments, int points) {
        this.title = title;
        this.content = content;
        this.url = url;
        this.description = description;
        this.commentAmount = comments;
        this.points = points;
    }

    public String getViewUrl() {
        return viewUrl;
    }

    public void setViewUrl(String viewUrl) {
        this.viewUrl = viewUrl;
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

    public int getCommentAmount() {
        return commentAmount;
    }

    public void setCommentAmount(int commentAmount) {
        this.commentAmount = commentAmount;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }
}
