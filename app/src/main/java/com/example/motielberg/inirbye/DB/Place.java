package com.example.motielberg.inirbye.DB;

import java.io.Serializable;

public class Place implements Serializable {
    String googleId,placeName,placeAddress,placeTel,icon,placeWebsite,placePhoto, openingTimes;
    int isOpen;
    double lat,lng;
    float rating;

    public Place(String googleId, String placeName, String placeAddress,String placeTel, String openingTimes,String placeWebsite, String placePhoto, String icon, double lat, double lng, float rating) {
        this.googleId = googleId;
        this.placeName = placeName;
        this.placeAddress = placeAddress;
        this.placeTel=placeTel;
        this.openingTimes=openingTimes;
//        this.isOpen=isOpen;
        this.placeWebsite=placeWebsite;
        this.placePhoto=placePhoto;
        this.icon = icon;
        this.lat = lat;
        this.lng = lng;
        this.rating=rating;
    }

    public String getOpeningTimes() {
        return openingTimes;
    }

    public int getIsOpen() {
        return isOpen;
    }

    public float getRating() {
        return rating;
    }

    public String getGoogleId() {
        return googleId;
    }

    public String getPlaceName() {
        return placeName;
    }

    public String getPlaceAddress() {
        return placeAddress;
    }

    public String getPlaceTel() {
        return placeTel;
    }

    public String getIcon() {
        return icon;
    }

    public String getPlaceWebsite() {
        return placeWebsite;
    }

    public String getPlacePhoto() {
        return placePhoto;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }
}