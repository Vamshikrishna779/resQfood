package com.example.resqfood;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.Cursor;


public class SQLDBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "ResqfoodDB";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "users";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_ROLE = "role";
    private static final String COLUMN_PROFILE_IMAGE = "profile_image";


    public SQLDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE_QUERY = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_NAME + " TEXT," +
                COLUMN_EMAIL + " TEXT," +
                COLUMN_PASSWORD + " TEXT," +
                COLUMN_ROLE + " TEXT," +
                COLUMN_PROFILE_IMAGE + " TEXT)";

        db.execSQL(CREATE_TABLE_QUERY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public long addUser(String name, String email, String password, String role) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_EMAIL, email);
        values.put(COLUMN_PASSWORD, password);
        values.put(COLUMN_ROLE, role);
        long result = db.insert(TABLE_NAME, null, values);
        db.close();
        return result;
    }


    public int updateUserProfileImage(String email, String profileImage) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PROFILE_IMAGE, profileImage);
        return db.update(TABLE_NAME, values, COLUMN_EMAIL + " = ?", new String[]{email});
    }

    public String getUserProfileImage(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COLUMN_PROFILE_IMAGE};
        String selection = COLUMN_EMAIL + " = ?";
        String[] selectionArgs = {email};
        Cursor cursor = db.query(TABLE_NAME, columns, selection, selectionArgs, null, null, null);
        String profileImage = null;
        if (cursor != null && cursor.moveToFirst()) {
            profileImage = cursor.getString(cursor.getColumnIndex(COLUMN_PROFILE_IMAGE));
            cursor.close();
        }
        db.close();
        return profileImage;
    }

    public String getUserRole(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COLUMN_ROLE};
        String selection = COLUMN_EMAIL + " = ?" + " AND " + COLUMN_PASSWORD + " = ?";
        String[] selectionArgs = {email, password};
        Cursor cursor = db.query(TABLE_NAME, columns, selection, selectionArgs, null, null, null);
        String role = null;
        if (cursor != null && cursor.moveToFirst()) {
            role = cursor.getString(cursor.getColumnIndex(COLUMN_ROLE));
            cursor.close();
        }
        db.close();
        return role;
    }
    public String getUserName(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COLUMN_NAME};
        String selection = COLUMN_EMAIL + " = ?";
        String[] selectionArgs = {email};
        Cursor cursor = db.query(TABLE_NAME, columns, selection, selectionArgs, null, null, null);
        String name = null;
        if (cursor != null && cursor.moveToFirst()) {
            name = cursor.getString(cursor.getColumnIndex(COLUMN_NAME));
            cursor.close();
        }
        db.close();
        return name;
    }

}
