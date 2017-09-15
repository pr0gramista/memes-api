package com.poprosturonin.data;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.LinkedList;
import java.util.List;

/**
 * Comment with optional responses
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Comment {
    private String content;
    private Author author;

    private List<Comment> responses = new LinkedList<>();
    private int points;
    private boolean isReply = false;

    public Comment(String content, Author author, int points) {
        this.content = content;
        this.author = author;
        this.points = points;
    }

    public Comment(String content, Author author, List<Comment> responses, int points) {
        this.content = content;
        this.author = author;
        this.responses = responses;
        this.points = points;
    }

    public boolean isReply() {
        return isReply;
    }

    public void setReply(boolean reply) {
        isReply = reply;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public List<Comment> getResponses() {
        return responses;
    }

    public void setResponses(List<Comment> responses) {
        this.responses = responses;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }
}
