package com.hastagqq.app.model;

import com.google.gson.annotations.Expose;

/**
 * @author avendael
 */
public class News {
    public News() {}

    public News(String title, String content, String location, String category) {
        this.title = title;
        this.content = content;
        this.location = location;
        this.category = category;
    }

    @Expose
    private String title;

    @Expose
    private String content;

    @Expose
    private String location;

    @Expose
    private String category;

    @Expose
    private long score;

    @Expose
    private long timestamp;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public long getScore() {
        return score;
    }

    public void setScore(long score) {
        this.score = score;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
