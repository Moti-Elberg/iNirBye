package com.example.motielberg.inirbye.ActivitiesAndFrags;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.SearchView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.motielberg.inirbye.Controller.PlaceAdapter;
import com.example.motielberg.inirbye.DB.Place;
import com.example.motielberg.inirbye.DB.PlaceDBHelper;
import com.example.motielberg.inirbye.DB.PlaceProvider;
import com.example.motielberg.inirbye.R;
import com.example.motielberg.inirbye.services.SearchService;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

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
import static com.example.motielberg.inirbye.DB.Constants.C_URI_SRC;
import static com.example.motielberg.inirbye.DB.Constants.PLACE;

public class SearchFrag extends Fragment implements View.OnClickListener, SearchView.OnQueryTextListener, Callback, SeekBar.OnSeekBarChangeListener {
    private PlaceAdapter adapter;
    private RecyclerView recyclerView;
    private SearchView searchView;
    private PlaceReceiver receiver;
    private SeekBar seekBar;
    private TextView txtRadius;

    public SearchFrag() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_search, container, false);
        v.findViewById(R.id.btnOpenFav).setOnClickListener(this);
        adapter = new PlaceAdapter(getContext(), 1);
        recyclerView = v.findViewById(R.id.recyclerView);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        searchView = v.findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(this);
        seekBar = v.findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(this);
        txtRadius=v.findViewById(R.id.txtRadius);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getContext());
        seekBar.setProgress(sp.getInt("radius", 1500));
        txtRadius.setText(seekBar.getProgress()+"");
        // register the PlaceReceiver
        receiver = new PlaceReceiver();
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(receiver, new IntentFilter(PLACE));

        return v;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(receiver);
    }

    @Override
    public void onStart() {
        super.onStart();
        getPlacesFromProvider();
    }

    private void getPlacesFromProvider() {
        Cursor cursor = getContext().getContentResolver().query(C_URI_SRC, null, null, null, null);
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
            double lat = cursor.getDouble(cursor.getColumnIndex(COL_LT));
            double lng = cursor.getDouble(cursor.getColumnIndex(COL_LG));
            float rate = cursor.getFloat(cursor.getColumnIndex(COL_RT));
            places.add(new Place(googleId, placeName, placeAddress, placeTel, openTime, placeWebsite, placePhoto, icon, lat, lng, rate));
        }
        adapter.setPlaces(places);
        cursor.close();
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getContext());
        if (query.length()<=0) {
            sp.edit().putString("query", "*").commit();
        } else {
            sp.edit().putString("query", query).commit();
        }
            adapter.clearSrcAdapter();
            // lower shitty keyboard. sorry for bad language Nir. but I hate Android. Apple for EVER!!!
            InputMethodManager lowKeyboard = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            lowKeyboard.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
            sp.edit().putInt("radius", seekBar.getProgress()).commit();
            getContext().startService(new Intent(getContext(), SearchService.class));

        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    @Override
    public void onFailure(Call call, IOException e) {

    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {

    }

    @Override
    public void onClick(View v) {
        getFragmentManager().beginTransaction()
                .replace(R.id.fragCon, new FavoritesFrag())
                .commit();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getContext());
        sp.edit().putInt("radius", seekBar.getProgress());
        txtRadius.setText(seekBar.getProgress()+"");
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }

    public class PlaceReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            getPlacesFromProvider();
        }
    }
}