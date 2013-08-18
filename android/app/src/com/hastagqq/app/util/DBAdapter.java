package com.hastagqq.app.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBAdapter {
    public static final String KEY_ROWID = "_id";
    public static final String KEY_CONTENT = "content";
    public static final String KEY_CATEGORY = "category";
    public static final String KEY_SCORE = "score";
    public static final String KEY_TITLE = "title";
    public static final String KEY_LOCATION = "location";
    
    private static final String TAG = DBAdapter.class.getSimpleName();
    
    private static final String DATABASE_NAME = "omnisens";
    private static final String DATABASE_TABLE = "news";
    private static final int DATABASE_VERSION = 1;
    
    private static final String DATABASE_CREATE = 
            "CREATE TABLE " + DATABASE_TABLE + " (" + KEY_ROWID + " INTEGER PRIMARY KEY AUTOINCREMENT, " 
                    + KEY_CONTENT + " TEXT NOT NULL, " 
                    + KEY_CATEGORY + " TEXT NOT NULL, " 
                    + KEY_SCORE + " INTEGER, " 
                    + KEY_LOCATION + " TEXT NOT NULL,"
                    + KEY_TITLE + " TEXT NOT NULL);";
    
    private final Context mContext;
    DatabaseHelper DBHelper;
    SQLiteDatabase mDb;
    
    public DBAdapter(Context ctx) {
        mContext = ctx;
        DBHelper = new DatabaseHelper(mContext);
    }
    
    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }
        
        @Override
        public void onCreate(SQLiteDatabase db) {
            try {
                db.execSQL(DATABASE_CREATE);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
            onCreate(db);
        }
    }
    
    public DBAdapter open() throws SQLException {
        mDb =DBHelper.getWritableDatabase();
        return this;
    }
    
    public void close() {
        DBHelper.close();
    }
    
    public long inserContact(String content, String category, 
            int score, String location, String title) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_CATEGORY, category);
        initialValues.put(KEY_CONTENT, content);
        initialValues.put(KEY_SCORE, score);
        initialValues.put(KEY_LOCATION, location);
        initialValues.put(KEY_TITLE, title);
        
        return mDb.insert(DATABASE_TABLE, null, initialValues);
    }
    
    public boolean deleteTable() {
        return mDb.delete(DATABASE_TABLE, null, null) > 0;
    }
    
    public Cursor getAllNews() {
        return mDb.query(DATABASE_TABLE, new String[] {KEY_ROWID, KEY_CATEGORY, KEY_CONTENT,
                KEY_SCORE, KEY_LOCATION, KEY_TITLE},
                null, null, null, null, null);
    }
    
    public Cursor getNews (long rowId) throws SQLException {
        Cursor cursor = mDb.query(true, DATABASE_TABLE, new String[]{KEY_ROWID, KEY_CATEGORY, KEY_CONTENT,
                KEY_SCORE, KEY_LOCATION, KEY_TITLE},  KEY_ROWID + "=" + rowId, null, null, null,
                null, null);
        
        if (cursor != null) {
            cursor.moveToNext();
        }
        return cursor;
    }
    
    public boolean updateContact(long rowId, String content, String category, 
            int score, String location, String title) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_CATEGORY, category);
        initialValues.put(KEY_CONTENT, content);
        initialValues.put(KEY_SCORE, score);
        initialValues.put(KEY_LOCATION, location);
        initialValues.put(KEY_TITLE, title);
        
        return mDb.update(DATABASE_TABLE, initialValues, KEY_ROWID + "=" + rowId, null) > 0;
    }
}
