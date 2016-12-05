package com.github.diwakar1988.junket.db;


import android.text.TextUtils;

import com.github.diwakar1988.junket.pojo.Location;

/**
 * Created by diwakar.mishra on 03/12/16.
 */

public class Settings {

    public static final int MIN_RECORDS=10;
    public static final int MIN_PHOTOS=5;
    public static final int MIN_RADIUS=3;

    public static final int MAX_RECORDS=30;
    public static final int MAX_PHOTOS=20;
    public static final int MAX_RADIUS=30;

    private int recordsPerPage=MIN_RECORDS;
    private int photosPerPage=MIN_PHOTOS;
    private int radius=MAX_RADIUS;
    private boolean useCurrentLocation=true;
    private Location customLocation;
    private Location currentLocation;

    private String lastQuery;

    public int getRecordsPerPage() {
        return recordsPerPage;
    }

    public void setRecordsPerPage(int recordsPerPage) {
        this.recordsPerPage = recordsPerPage;
    }

    public int getPhotosPerPage() {
        return photosPerPage;
    }

    public void setPhotosPerPage(int photosPerPage) {
        this.photosPerPage = photosPerPage;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public boolean useCurrentLocation() {
        return useCurrentLocation;
    }

    public void setUseCurrentLocation(boolean useCurrent) {
        this.useCurrentLocation = useCurrent;
    }

    public Location getCustomLocation() {
        return customLocation;
    }

    public void setCustomLocation(Location customLocation) {
        this.customLocation = customLocation;
    }

    public String getLastQuery() {
        return TextUtils.isEmpty(lastQuery)?"":lastQuery;
    }

    public void setLastQuery(String lastQuery) {
        this.lastQuery = lastQuery;
    }

    public void clearLastQuery() {
        this.lastQuery = "";
    }

    public void setCurrentLocation(double lat,double lng) {
        this.currentLocation = new Location();
        this.currentLocation.lat=lat;
        this.currentLocation.lng=lng;
    }

    public Location getCurrentLocation() {
        return currentLocation;
    }
    public boolean hastCurrentLocation() {
        return currentLocation!=null && currentLocation.lat>0 && currentLocation.lng>0;
    }

    public void resetCurrentLocation() {
        this.currentLocation=null;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {

        Settings settings = new Settings();
        settings.setCustomLocation(getCustomLocation());
        settings.setUseCurrentLocation(useCurrentLocation());
        if (getCurrentLocation()!=null) {
            settings.setCurrentLocation(getCurrentLocation().lat, getCurrentLocation().lng);
        }
        settings.setLastQuery(getLastQuery());
        settings.setPhotosPerPage(getPhotosPerPage());
        settings.setRadius(getRadius());
        settings.setRecordsPerPage(getRecordsPerPage());
        return settings;
    }


}