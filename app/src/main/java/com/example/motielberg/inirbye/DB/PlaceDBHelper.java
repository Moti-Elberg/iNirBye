package com.example.motielberg.inirbye.DB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import static com.example.motielberg.inirbye.DB.Constants.COL_ADD;
import static com.example.motielberg.inirbye.DB.Constants.COL_ICON;
import static com.example.motielberg.inirbye.DB.Constants.COL_ID;
import static com.example.motielberg.inirbye.DB.Constants.COL_IMG;
import static com.example.motielberg.inirbye.DB.Constants.COL_LG;
import static com.example.motielberg.inirbye.DB.Constants.COL_LT;
import static com.example.motielberg.inirbye.DB.Constants.COL_NAME;
import static com.example.motielberg.inirbye.DB.Constants.COL_OPT;
import static com.example.motielberg.inirbye.DB.Constants.COL_RT;
import static com.example.motielberg.inirbye.DB.Constants.COL_TEL;
import static com.example.motielberg.inirbye.DB.Constants.COL_WEB;
import static com.example.motielberg.inirbye.DB.Constants.TABLE_FAV;
import static com.example.motielberg.inirbye.DB.Constants.TABLE_SRC;

public class PlaceDBHelper extends SQLiteOpenHelper {

    public PlaceDBHelper(Context context) {
        super(context, "placeDB", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sqlSrc = String.format("create table %s(%s text primary key, %s text, %s text, %s text, %s text, %s text, %s text, %s text, %s real, %s real, %s real)",
                TABLE_SRC, COL_ID, COL_NAME, COL_ADD, COL_OPT, COL_TEL, COL_WEB, COL_IMG, COL_ICON, COL_LT, COL_LG,COL_RT);
        db.execSQL(sqlSrc);

        String sqlFav = String.format("create table %s(%s text primary key, %s text, %s text, %s text, %s text, %s text, %s text, %s text, %s real, %s real, %s real)",
                TABLE_FAV, COL_ID, COL_NAME, COL_ADD, COL_OPT, COL_TEL, COL_WEB, COL_IMG, COL_ICON, COL_LT, COL_LG,COL_RT);
        db.execSQL(sqlFav);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}