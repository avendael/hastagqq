package com.hastagqq.app.db;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.hastagqq.app.model.News;

/**
 * @author avendael
 */
public class DatabaseHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "omnisens";

    public DatabaseHandler(Context context, String name, SQLiteDatabase.CursorFactory factory,
                           int version) {
        super(context, name, factory, version);
    }

    public DatabaseHandler(Context context, String name, SQLiteDatabase.CursorFactory factory,
                           int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(News.SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(News.SQL_DROP_TABLE);

        db.execSQL(News.SQL_CREATE_TABLE);
    }
}
