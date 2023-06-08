/*
package com.example.myapplication;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;

public class gps_db extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "GPS_location.db";
    private static final String TABLE_NAME = "location_table";
    private static final String COL_ID = "id";
    private static final String COL_NO = "no";
    private static final String COL_LATITUDE = "latitude";
    private static final String COL_LONGITUDE = "longitude";
    private static final String COL_TIME = "time";

    public gps_db(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableQuery = "CREATE TABLE " + TABLE_NAME + " (" +
                COL_NO + " INTEGER AUTOINCREMENT, " +
                COL_ID + " INTEGER PRIMARY KEY, " + //기본키
                COL_LATITUDE + " REAL, " +
                COL_LONGITUDE + " REAL, " +
                COL_TIME + " TEXT);";
        db.execSQL(createTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void insertLocation(double latitude, double longitude, String time) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_LATITUDE, latitude);
        values.put(COL_LONGITUDE, longitude);
        values.put(COL_TIME, time);
        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    public boolean checkDateExists(String date) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COL_TIME + " LIKE '" + date + "%'";
        Cursor cursor = db.rawQuery(query, null);
        boolean exists = cursor.moveToFirst();
        cursor.close();
        return exists;
    }

    public void resetDatabase() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_NAME);
        db.close();
    }

    public void insertDate(String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_TIME, date + " 00:00:00");
        db.insert(TABLE_NAME, null, values);
        db.close();
    }
}

*/
