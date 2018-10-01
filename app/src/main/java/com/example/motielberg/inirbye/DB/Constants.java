package com.example.motielberg.inirbye.DB;

import android.net.Uri;

public class Constants {

    public static final String TABLE_FAV = "placesFav";
    public static final String TABLE_SRC = "placesSrc";
    public static final String COL_ID = "googleId";
    public static final String COL_TEL = "placeTel";
    public static final String COL_OPT = "openingTimes";
    public static final String COL_OP = "isOpen";
    public static final String COL_NAME = "placeName";
    public static final String COL_ADD = "placeAddress";
    public static final String COL_ICON = "icon";
    public static final String COL_WEB = "placeWebsite";
    public static final String COL_IMG = "placePhoto";
    public static final String COL_LT = "lat";
    public static final String COL_LG = "lng";
    public static final String COL_RT = "rate";
    public static final String AUTHORITY = "com.jbt.inirbye";
    public static final String PLACE = "place";
    public static final Uri C_URI_FAV = Uri.parse("content://" + AUTHORITY + "/" + TABLE_FAV);
    public static final Uri C_URI_SRC = Uri.parse("content://" + AUTHORITY + "/" + TABLE_SRC);
    public static final int RC = 666;
    public static final int MI = 620;
    public static final int KM = 1000;
}
