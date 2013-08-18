package com.hastagqq.app.model;

import android.database.Cursor;
import android.provider.BaseColumns;

import com.google.gson.annotations.Expose;

/**
 * @author avendael
 */
public class News implements BaseColumns {
    public static final String TABLE_NAME = "news";

    // Database columns
    public static final String TITLE = "title";
    public static final String CONTENT = "content";
    public static final String LOCATION = "location";
    public static final String CATEGORY = "category";
    public static final String SCORE = "score";
    public static final String TIMESTAMP = "timestamp";

    // DB boilerplate
    public static final String SQL_CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " ("
            + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + TITLE + " TEXT, "
            + CONTENT + "TEXT, "
            + LOCATION + "TEXT, "
            + CATEGORY + "TEXT, "
            + SCORE + "INTEGER, "
            + TIMESTAMP + "TEXT"
            + ");";
    public static final String SQL_DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

    public News() {}

    public News(String title, String content, String location, String category) {
        this.title = title;
        this.content = content;
        this.location = location;
        this.category = category;
    }

    public News(Cursor cursor) {
        this.title = cursor.getString(cursor.getColumnIndex(TITLE));
        this.content = cursor.getString(cursor.getColumnIndex(CONTENT));
        this.category = cursor.getString(cursor.getColumnIndex(CATEGORY));
        this.location = cursor.getString(cursor.getColumnIndex(LOCATION));
        this.score = cursor.getLong(cursor.getColumnIndex(SCORE));
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
