package com.hastagqq.app.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.hastagqq.app.model.News;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author avendael
 */
public class NewsDal {
    public static final String[] NEWS_PROJECTION = {
            News._ID, News.TITLE, News.CONTENT, News.CATEGORY, News.LOCATION,
            News.SCORE
    };

    private SQLiteDatabase mDatabase;
    private DatabaseHandler mDatabaseHandler;

    public NewsDal(Context context) {
        mDatabaseHandler = new DatabaseHandler(context);
    }

    public void open() throws SQLException {
        mDatabase = mDatabaseHandler.getReadableDatabase();
    }

    public void close() {
        mDatabase.close();
    }

    public long createNews(News news) {
        ContentValues values = new ContentValues();

        values.put(News.TITLE, news.getTitle());
        values.put(News.CONTENT, news.getContent());
        values.put(News.CATEGORY, news.getCategory());
        values.put(News.LOCATION, news.getLocation());
        values.put(News.SCORE, news.getScore());
//        values.put(News.TIMESTAMP, news.getTimestamp());

        return mDatabase.insert(News.TABLE_NAME, null, values);
    }

    public long deleteAllNews() {
        return mDatabase.delete(News.TABLE_NAME, null, null);
    }

    public News getNews(String column, String operator, String arg) {
        Cursor cursor = mDatabase.query(News.TABLE_NAME, NEWS_PROJECTION, column + operator + "?",
                new String[] { arg }, null, null, null);
        News news = null;

        if (cursor != null) {
            if (cursor.moveToFirst()) news = new News(cursor);

            cursor.close();
        }

        return news;
    }

    public List<News> getAllNews() {
        Cursor cursor = mDatabase.query(News.TABLE_NAME, NEWS_PROJECTION, null, null, null, null,
                null);
        List<News> newsList = new ArrayList<News>();

        if (cursor != null) {
            while (cursor.moveToNext()) newsList.add(new News(cursor));

            cursor.close();
        }

        return newsList;
    }
}
