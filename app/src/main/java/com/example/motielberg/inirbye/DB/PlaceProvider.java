package com.example.motielberg.inirbye.DB;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class PlaceProvider extends ContentProvider {

    private PlaceDBHelper helper;

    public PlaceProvider() {
    }

    @Override
    public boolean onCreate() {
        helper = new PlaceDBHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    // insert items to DB
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        SQLiteDatabase db = helper.getWritableDatabase();
        long row = db.insert(uri.getLastPathSegment(), null, values);
        // db.close(); // TODO cry to Nir, closing the DB crushes the app because of connection pool has been closed.
        return Uri.withAppendedPath(uri, row+"");
    }

    @Nullable
    @Override
    // finds items from DB
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query(uri.getLastPathSegment(), projection, selection, selectionArgs, null, null, sortOrder);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }


    @Override
    // delete from DB
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase db = helper.getWritableDatabase();
        int count = db.delete(uri.getLastPathSegment(), selection, selectionArgs);
//        db.close();
        return count;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}
