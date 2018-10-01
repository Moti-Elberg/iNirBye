package com.example.motielberg.inirbye.ActivitiesAndFrags;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.motielberg.inirbye.DB.Place;
import com.example.motielberg.inirbye.R;
import com.google.android.gms.maps.model.LatLng;
import com.squareup.picasso.Picasso;

import me.zhanghai.android.materialratingbar.MaterialRatingBar;

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

public class InfoFrag extends Fragment implements View.OnClickListener {
    private TextView name, address, openhours, txtTel, txtWeb;
    private Place place;
    private LatLng latLng;
    private SharedPreferences sp;
    private ImageView imageView, btnFav;
    private MaterialRatingBar ratingBar;
    private boolean isFav;

    public InfoFrag() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_info, container, false);
        name = v.findViewById(R.id.txtPlaceName);
        ratingBar = v.findViewById(R.id.ratingBar);
        address = v.findViewById(R.id.txtAddress);
        txtWeb = v.findViewById(R.id.txtWeb);
        txtWeb.setOnClickListener(this);
        txtTel = v.findViewById(R.id.txtTel);
        txtTel.setOnClickListener(this);
        openhours = v.findViewById(R.id.openHours);
        imageView = v.findViewById(R.id.imageView);
        v.findViewById(R.id.btnWeb).setOnClickListener(this);
        v.findViewById(R.id.btnOpenFav).setOnClickListener(this);
        v.findViewById(R.id.btnOpenSrc).setOnClickListener(this);
        v.findViewById(R.id.btnCall).setOnClickListener(this);
        btnFav = v.findViewById(R.id.btnFav);
        btnFav.setOnClickListener(this);
        // get serializable of "place" and stores in shared prefs.
        sp = PreferenceManager.getDefaultSharedPreferences(getContext());
        if (getArguments() != null) {
            place = (Place) getArguments().getSerializable("place");
            sp.edit().putString("id", place.getGoogleId()).apply();
            sp.edit().putString("address", place.getPlaceAddress()).apply();
            sp.edit().putString("tel", place.getPlaceTel()).apply();
            sp.edit().putString("openhours", place.getOpeningTimes()).apply();
            sp.edit().putInt("isOpen", place.getIsOpen()).apply();
            sp.edit().putString("website", place.getPlaceWebsite()).apply();
            sp.edit().putString("imageView", place.getPlacePhoto()).apply();
            sp.edit().putFloat("rate", place.getRating()).apply();
            latLng = new LatLng(place.getLat(), place.getLng());
            showPlace(place);
        }
//        else {
//            name.setText(sp.getString("name", "name"));
//            address.setText(sp.getString("address", "address"));
//            txtTel.setText(sp.getString("tel", "tel"));
//            openhours.setText(sp.getString("openhours", "openhours"));
//            txtWeb.setText(sp.getString("website", "website"));
//            ratingBar.setNumStars((int) sp.getFloat("rate", 0));
//            Picasso.get().load(sp.getString("imageView","imageView" )).into(imageView);
//        }
        // check if the place already exists on Favorites DB - heart on-off
        Cursor cursor = getContext().getContentResolver().query(C_URI_FAV, null, COL_ID + "=?",
                new String[]{sp.getString("id", "id")}, null);
        if (cursor.getCount() == 1) {
            btnFav.setImageResource(R.drawable.heart);
            isFav = true;
        } else {
            btnFav.setImageResource(R.drawable.heartg);
            isFav = false;
        }
        return v;
    }

    public void showPlace(Place place) {
        name.setText(place.getPlaceName());
        address.setText(place.getPlaceAddress());
        txtWeb.setText(place.getPlaceWebsite());
        txtTel.setText(place.getPlaceTel());
        openhours.setText(place.getOpeningTimes());
        ratingBar.setRating(place.getRating());
        Picasso.get().load(place.getPlacePhoto()).into(imageView);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnOpenFav:
                // open favorite frag
                getFragmentManager().beginTransaction()
                        .replace(R.id.fragCon, new FavoritesFrag())
                        .commit();
                break;
            case R.id.btnOpenSrc:
                // open search frag
                getFragmentManager().beginTransaction()
                        .replace(R.id.fragCon, new SearchFrag())
                        .commit();
                break;
            case R.id.btnCall:
                // call place1
                Intent callInt = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + sp.getString("tel", "tel")));
                startActivity(callInt);
                break;
            case R.id.txtTel:
                // call place2
                Intent telInt2 = new Intent(Intent.ACTION_VIEW, Uri.parse("tel:" + txtTel.getText().toString()));
                startActivity(telInt2);
                break;
            case R.id.btnWeb:
                // open webpage1
                Intent webInt = new Intent(Intent.ACTION_VIEW, Uri.parse(place.getPlaceWebsite()));
                startActivity(webInt);
                break;
            case R.id.txtWeb:
                // open webpage2
                Intent webInt2 = new Intent(Intent.ACTION_VIEW, Uri.parse(txtWeb.getText().toString()));
                startActivity(webInt2);
                break;
            case R.id.btnFav:
                if (!isFav) {
                    btnFav.setImageResource(R.drawable.heart);
                    isFav = true;
                    // saves to Favorites DB
                    saveFavToDB();
                } else {
                    btnFav.setImageResource(R.drawable.heartg);
                    isFav = false;
                    // delete favorite item from DB
                    delFavFromDB();
                }
                break;
        }
    }

    public void saveFavToDB() {
        ContentValues values = new ContentValues();
        values.put(COL_ID, place.getGoogleId());
        values.put(COL_NAME, place.getPlaceName());
        values.put(COL_ADD, place.getPlaceAddress());
        values.put(COL_TEL, place.getPlaceTel());
        values.put(COL_OPT, place.getOpeningTimes());
        values.put(COL_WEB, place.getPlaceWebsite());
        values.put(COL_IMG, place.getPlacePhoto());
        values.put(COL_ICON, place.getIcon());
        values.put(COL_LT, place.getLat());
        values.put(COL_LG, place.getLng());
        values.put(COL_RT, place.getRating());
        getContext().getContentResolver().insert(C_URI_FAV, values);
    }

    public void delFavFromDB() {
        getContext().getContentResolver().delete(C_URI_FAV, COL_ID + "='" + place.getGoogleId() + "'", null);
    }
}