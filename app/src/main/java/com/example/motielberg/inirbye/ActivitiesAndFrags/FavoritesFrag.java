package com.example.motielberg.inirbye.ActivitiesAndFrags;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.motielberg.inirbye.Controller.PlaceAdapter;
import com.example.motielberg.inirbye.DB.Place;
import com.example.motielberg.inirbye.R;

import java.util.ArrayList;

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
import static com.example.motielberg.inirbye.DB.Constants.C_URI_FAV;

public class FavoritesFrag extends Fragment implements View.OnClickListener, View.OnLongClickListener {
    private PlaceAdapter adapter;
    private RecyclerView recyclerView;

    public FavoritesFrag() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_favorites, container, false);
        v.findViewById(R.id.btnOpenSrc).setOnClickListener(this);
        adapter = new PlaceAdapter(getContext(), 2);
        v.findViewById(R.id.delFav).setOnLongClickListener(this);
        recyclerView = v.findViewById(R.id.recyclerView);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        getPlacesFromDB();

        return v;
    }

    private void getPlacesFromDB() {
        Cursor cursor = getContext().getContentResolver().query(C_URI_FAV, null, null, null, null);
        ArrayList<Place> places = new ArrayList<>();
        places.clear();
        while (cursor.moveToNext()) {
            String googleId = cursor.getString(cursor.getColumnIndex(COL_ID));
            String placeName = cursor.getString(cursor.getColumnIndex(COL_NAME));
            String placeAddress = cursor.getString(cursor.getColumnIndex(COL_ADD));
            String placeTel = cursor.getString(cursor.getColumnIndex(COL_TEL));
            String openTime = cursor.getString(cursor.getColumnIndex(COL_OPT));
            String placePhoto = cursor.getString(cursor.getColumnIndex(COL_IMG));
            String icon = cursor.getString(cursor.getColumnIndex(COL_ICON));
            String placeWebsite = cursor.getString(cursor.getColumnIndex(COL_WEB));
            float lat = cursor.getFloat(cursor.getColumnIndex(COL_LT));
            float lng = cursor.getFloat(cursor.getColumnIndex(COL_LG));
            float rate = cursor.getFloat(cursor.getColumnIndex(COL_RT));
            places.add(new Place(googleId, placeName, placeAddress, placeTel, openTime, placeWebsite, placePhoto, icon, lat, lng, rate));
        }
        cursor.close();
        adapter.setPlaces(places);
    }
    // button back to search frag
    @Override
    public void onClick(View v) {
        getFragmentManager().beginTransaction()
                .replace(R.id.fragCon, new SearchFrag())
                .commit();
    }
    // clears the favorites list on long click
    @Override
    public boolean onLongClick(View v) {
        adapter.clearFavDB();
        return true;
    }
}