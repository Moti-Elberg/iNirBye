package com.example.motielberg.inirbye.Controller;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.location.Location;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.motielberg.inirbye.DB.Place;
import com.example.motielberg.inirbye.R;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;

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
import static com.example.motielberg.inirbye.DB.Constants.C_URI_SRC;

public class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.PlaceHolder> {
    private Context context;
    private ArrayList<Place> places;
    private int frag;
    private float disCalc;
    private OnItemSelectListener listener;
    private double latitude, longitude;


    public PlaceAdapter(Context context, int frag) {
        this.context = context;
        this.frag = frag;
        listener = (OnItemSelectListener) context;
    }

    public void setPlaces(ArrayList<Place> places) {
        this.places = places;
        notifyDataSetChanged();

        // get saved strings from SP
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

        latitude = sp.getFloat("latitude", 0);
        longitude = sp.getFloat("longitude", 0);

    }

    @NonNull
    @Override
    public PlaceHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PlaceHolder(LayoutInflater.from(context).inflate(R.layout.item_view, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull PlaceHolder holder, int position) {
        holder.bind(places.get(position));
    }

    @Override
    public int getItemCount() {
        if (places == null) {
            return 0;
        }
        return places.size();
    }

    // binds between item_view.xml layout with name, address & icon from
    public class PlaceHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView txtPlaceName, txtAddress, txtDstM, txtDstT;
        private ImageView icon, btnFav;
        private MaterialRatingBar ratingBar;
        private Place place1;
        private boolean isFav;


        public PlaceHolder(View itemView) {
            super(itemView);
            txtPlaceName = itemView.findViewById(R.id.txtPlaceName);
            txtAddress = itemView.findViewById(R.id.txtAddress);
            txtDstM = itemView.findViewById(R.id.txtDstM);
            txtDstT = itemView.findViewById(R.id.txtDstT);
            icon = itemView.findViewById(R.id.icon);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            btnFav = itemView.findViewById(R.id.btnFav);
            itemView.setOnClickListener(this);
            btnFav.setOnClickListener(this);
        }

        public void bind(Place place) {
            txtPlaceName.setText(place.getPlaceName());
            txtAddress.setText(place.getPlaceAddress());
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
            if (sp.getBoolean("distance", true)){
                txtDstT.setText("km");
            } else{
                txtDstT.setText("mi");
            }

            txtDstM.setText(calcDistance(place.getLng(), place.getLat()) + "");
            ratingBar.setRating(place.getRating());
            Picasso.get().load(place.getIcon()).into(icon);

            Cursor cursor = context.getContentResolver().query(C_URI_FAV, null, COL_ID + "=?",
                    new String[]{place.getGoogleId()}, null);
            if (cursor.getCount() == 1) {
                btnFav.setImageResource(R.drawable.heart);
                isFav = true;
            } else {
                btnFav.setImageResource(R.drawable.heartg);
                isFav = false;
            }
            place1 = place;
        }

        // send the selected itemView - place - from the places arrayList by adapter position
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btnFav:
                    if (!isFav) {
                        btnFav.setImageResource(R.drawable.heart);
                        isFav = true;
                        saveFavToDB(place1);
                    } else {
                        btnFav.setImageResource(R.drawable.heartg);
                        isFav = false;
                        delFavPlace(place1);
                    }
                    break;

                default:
                    listener.sendPlace(places.get(getAdapterPosition()));
            }
        }
    }

    // sends a place object via interface
    public interface OnItemSelectListener {
        void sendPlace(Place place);
    }

    public void clearSrcAdapter() {
        places.clear();
        notifyDataSetChanged();
        context.getContentResolver().delete(C_URI_SRC, null, null);
    }

    public void clearFavDB() {
        places.clear();
        notifyDataSetChanged();
        context.getContentResolver().delete(C_URI_FAV, null, null);
    }

    public double calcDistance(double longitude1, double latitude1) {
        Location userLoc = new Location("");
        userLoc.setLongitude(longitude);
        userLoc.setLatitude(latitude);

        Location placeLoc = new Location("");
        placeLoc.setLongitude(longitude1);
        placeLoc.setLatitude(latitude1);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        double calc;
        if (sp.getBoolean("distance", true)){
            calc= 1000.0;
        } else{
            calc=1600.0;
        }

        double distance = (userLoc.distanceTo(placeLoc)) / calc;
        DecimalFormat dist = new DecimalFormat("0.00");
        distance = Double.parseDouble(dist.format(distance));
        return distance;
    }

    public void delFavPlace(Place place) {
        context.getContentResolver().delete(C_URI_FAV, COL_ID + "=?", new String[]{place.getGoogleId()});
    }

    public void saveFavToDB(Place place) {
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
        context.getContentResolver().insert(C_URI_FAV, values);
    }
}