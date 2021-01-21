package com.flickagram;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHelper";

    private static final String TABLE_NAME = "Flickr_Table";
    private static final String COL1 = "Image_id";
    private static final String COL2 = "Image_path";
    private static final String COL3 = "Image_title";
    private static final String COL4 = "Image_link";


    public DatabaseHelper(Context context) {
        super(context, TABLE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String createTable = "CREATE TABLE " + TABLE_NAME + " ("+ COL1 +" TEXT, " +
                COL2 +" TEXT ," + COL3 +" TEXT ," + COL4 +" TEXT)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }


    public boolean addData(String image_id, String image_path, String image_title, String image_link) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL1, image_id);
        contentValues.put(COL2, image_path);
        contentValues.put(COL3, image_title);
        contentValues.put(COL4, image_link);
        long result = db.insert(TABLE_NAME, null, contentValues);

        //if date as inserted incorrectly it will return -1
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    public Cursor getData(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME;
        Cursor data = db.rawQuery(query, null);
        return data;
    }


    public Cursor getItem(String image_id){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT " + COL2 + "," + COL3 +  "," + COL4 + "  FROM " + TABLE_NAME +
                " WHERE " + COL1 + " = '" + image_id + "'";
        Cursor data = db.rawQuery(query, null);
        return data;
    }

}
