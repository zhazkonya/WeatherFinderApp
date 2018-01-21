package com.example.zoskenbaeva.weathersearching;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by zhaziraoskenbayeva on 21/01/18.
 */

public class DHCache extends SQLiteOpenHelper {
    static final String DB_NAME = "cache";
    static final String  TB_NAME = "w_results";
    static final String CREATE_TB_CACHE = "create table "
            +TB_NAME+" (\n" +
            "    w_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,\n" +
            "    w_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,\n" +
            "    w_token varchar(20),\n" +
            "    w_result varchar(100),\n" +
            "    w_data varchar(255),\n"+
            "    w_icon varchar(10));";
    static final int DB_VERSION = 1;

    DHCache(Context context){
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TB_CACHE); }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TB_NAME);
        onCreate(db);
    }

}