package com.cabot.androidtemplateproject.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by neethu on 18/7/17.
 *
 * database creation and update
 *
 */

public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "app_name_record.db";

    private static final int DB_VERSION_NO = 1;

    private static DBHelper mInstance = null;

    public DBHelper(Context mContext) {
        super(mContext, DATABASE_NAME, null, DB_VERSION_NO);
    }

    public static synchronized DBHelper getInstance(Context mContext) {

        if (mInstance == null) {
            mInstance = new DBHelper(mContext);
        }
        return mInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
