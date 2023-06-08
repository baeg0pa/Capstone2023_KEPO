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
    private static final String COL_LATITUDE = "latitude";
    private static final String COL_LONGITUDE = "longitude";
    private static final String COL_TIME = "time";
    private final SQLiteDatabase db; // SQLiteDatabase 멤버 변수 추가
    public gps_db(Context context) {
        super(context, DATABASE_NAME, null, 1);
        db = getWritableDatabase(); // getWritableDatabase로 초기화
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableQuery = "CREATE TABLE " + TABLE_NAME + "(" +
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
        db.beginTransaction(); // 트랜잭션 시작
        try {
            ContentValues values = new ContentValues();
            values.put(COL_LATITUDE, latitude);
            values.put(COL_LONGITUDE, longitude);
            values.put(COL_TIME, time);
            db.insert(TABLE_NAME, null, values);
            db.setTransactionSuccessful(); // 트랜잭션 성공으로 마킹
        } finally {
            db.endTransaction(); // 트랜잭션 종료
        }
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
        db.delete(TABLE_NAME, null, null); // 테이블 내용 삭제
    }

    public void insertDate(String date) {
        ContentValues values = new ContentValues();
        values.put(COL_TIME, date + " 00:00:00");
        db.insert(TABLE_NAME, null, values);
    }
}
