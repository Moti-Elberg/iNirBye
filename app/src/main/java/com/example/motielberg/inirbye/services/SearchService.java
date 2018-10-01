package com.example.motielberg.inirbye.services;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.example.motielberg.inirbye.DB.Constants.COL_IMG;
import static com.example.motielberg.inirbye.DB.Constants.COL_OPT;
import static com.example.motielberg.inirbye.DB.Constants.COL_RT;
import static com.example.motielberg.inirbye.DB.Constants.COL_TEL;
import static com.example.motielberg.inirbye.DB.Constants.COL_WEB;
import static com.example.motielberg.inirbye.DB.Constants.C_URI_FAV;
import static com.example.motielberg.inirbye.DB.Constants.C_URI_SRC;
import static com.example.motielberg.inirbye.DB.Constants.PLACE;
import static com.example.motielberg.inirbye.DB.Constants.COL_ADD;
import static com.example.motielberg.inirbye.DB.Constants.COL_ICON;
import static com.example.motielberg.inirbye.DB.Constants.COL_ID;
import static com.example.motielberg.inirbye.DB.Constants.COL_LG;
import static com.example.motielberg.inirbye.DB.Constants.COL_LT;
import static com.example.motielberg.inirbye.DB.Constants.COL_NAME;

public class SearchService extends IntentService {

    private float latitude, longitude;
    private int radius;
    private String query;

    public SearchService() {
        super("SearchService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        // get saved strings from SP
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        latitude = sp.getFloat("latitude", 0);
        longitude = sp.getFloat("longitude", 0);
        radius = sp.getInt("radius", 1500);
        query = sp.getString("query", "shit");
        // delete search list every new search
        if (C_URI_SRC != null) {
            getContentResolver().delete(C_URI_SRC, null, null);
        }

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="
                        + latitude + "," + longitude
                        + "&radius=" + radius
                        + "&keyword=" + query
                        + "&key=AIzaSyB175nscszoNTUixJT5BIKDbS7aH44CsSk")
                .build();
        try {
            Response response = client.newCall(request).execute();
            JSONObject jsonObject = new JSONObject(response.body().string());
            JSONArray rootArray = jsonObject.getJSONArray("results");
            for (int i = 0; i < rootArray.length(); i++) {
                // find the Object inside the Array
                JSONObject postObj = rootArray.getJSONObject(i);

                // get the info into string for each item
                String googleId = postObj.getString("place_id");
                Request request2 = new Request.Builder()
                        .url("https://maps.googleapis.com/maps/api/place/details/json?placeid="
                                + googleId
                                + "&key=AIzaSyB175nscszoNTUixJT5BIKDbS7aH44CsSk")
                        .build();

                Response response2 = client.newCall(request2).execute();
                JSONObject jsonInfo = new JSONObject(response2.body().string());

                String telNumber = "";
                if (jsonInfo.getJSONObject("result").has("international_phone_number")) {
                    telNumber = jsonInfo.getJSONObject("result").getString("international_phone_number");
                }

                String d0;
                String openingTimes = "";
                if (jsonInfo.getJSONObject("result").has("opening_hours")) {
                    d0 = jsonInfo.getJSONObject("result").getJSONObject("opening_hours").getJSONArray("weekday_text").getString(0);
                    String d1 = jsonInfo.getJSONObject("result").getJSONObject("opening_hours").getJSONArray("weekday_text").getString(1);
                    String d2 = jsonInfo.getJSONObject("result").getJSONObject("opening_hours").getJSONArray("weekday_text").getString(2);
                    String d3 = jsonInfo.getJSONObject("result").getJSONObject("opening_hours").getJSONArray("weekday_text").getString(3);
                    String d4 = jsonInfo.getJSONObject("result").getJSONObject("opening_hours").getJSONArray("weekday_text").getString(4);
                    String d5 = jsonInfo.getJSONObject("result").getJSONObject("opening_hours").getJSONArray("weekday_text").getString(5);
                    String d6 = jsonInfo.getJSONObject("result").getJSONObject("opening_hours").getJSONArray("weekday_text").getString(6);
                    openingTimes = d6 + "\n" + d0 + "\n" + d1 + "\n" + d2 + "\n" + d3 + "\n" + d4 + "\n" + d5;
                }
                String website = "";
                if (jsonInfo.getJSONObject("result").has("website")) {
                    website = jsonInfo.getJSONObject("result").getString("website");
                }

                String placeName = postObj.getString("name");

                String placeAddress = "";
                if (postObj.has("vicinity")) {
                    placeAddress = postObj.getString("vicinity");
                }
                String icon = "";
                if (postObj.has("icon")) {
                    icon = postObj.getString("icon");
                }

                double lat = postObj.getJSONObject("geometry").getJSONObject("location").getDouble("lat");
                double lng = postObj.getJSONObject("geometry").getJSONObject("location").getDouble("lng");

                float placeRate = 0;
                if (postObj.has("rating")) {
                    placeRate = (float) postObj.getDouble("rating");
                }

                String photoRef;
                String placePhoto = "";
                if (postObj.getJSONArray("photos").getJSONObject(0).has("photo_reference")) {
                    photoRef = postObj.getJSONArray("photos").getJSONObject(0).getString("photo_reference");
                    placePhoto = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=1600&photoreference="
                            + photoRef
                            + "&key=AIzaSyB175nscszoNTUixJT5BIKDbS7aH44CsSk";
                }
                // saves json data to DB
                ContentValues values = new ContentValues();
                values.put(COL_ID, googleId);
                values.put(COL_NAME, placeName);
                values.put(COL_ADD, placeAddress);
                values.put(COL_TEL, telNumber);
                values.put(COL_OPT, openingTimes);
                values.put(COL_WEB, website);
                values.put(COL_IMG, placePhoto);
                values.put(COL_ICON, icon);
                values.put(COL_LT, lat);
                values.put(COL_LG, lng);
                values.put(COL_RT, placeRate);
                getContentResolver().insert(C_URI_SRC, values);
            }

            // Send local broadcast
            Intent broadcastIntent = new Intent(PLACE);
            // send the broadcast to the application
            LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}