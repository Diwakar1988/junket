package com.github.diwakar1988.junket.db;

import android.content.Context;

import com.github.diwakar1988.junket.Junket;
import com.github.diwakar1988.junket.db.dao.Review;
import com.github.diwakar1988.junket.pojo.Venue;

import java.io.ObjectStreamException;
import java.util.ArrayList;

/**
 * Created by diwakar.mishra on 02/12/16.
 */


public final class DataController {

    private volatile static DataController instance;

    public static  void init() {
        getInstance();
    }
    public static DataController getInstance() {
        if (instance==null){
            synchronized (DataController.class){
                if (instance==null){
                    instance=new DataController(Junket.getInstance());
                }
            }
        }
        return instance;
    }
    private Settings settings;
    private SQLDataBaseHandler sqlDataBaseHandler;

    private DataController(Context context){
        if (instance!=null){
            //prevent reflection
            throw new IllegalStateException("Instance already initialized");
        }
        AppPreferences.init(context);
        settings = AppPreferences.getInstance().loadSettings();
        sqlDataBaseHandler=new SQLDataBaseHandler(context);
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return getInstance();
    }
    private Object readResolve() throws ObjectStreamException {
        // prevent d serialization
        return getInstance();
    }

    public Settings getSettings() {
        return settings;
    }
    public void saveSettings(){
        AppPreferences.getInstance().saveSettings(settings);
    }

    public void saveReview(Review review) {

        sqlDataBaseHandler.saveReview(review);

    }

    public Review getReview(String venueId){
        return sqlDataBaseHandler.getReview(venueId);
    }


    public void saveFavoriteVenue(Venue venue) {
        sqlDataBaseHandler.saveFavoriteVenue(venue);
    }
    public boolean hasFavoriteVenue(String venueId) {
        return sqlDataBaseHandler.getFavoriteVenue(venueId)!=null;
    }
    public boolean hasFavorites() {
        return sqlDataBaseHandler.getFavoriteVenueCount()>0;
    }
    public long getFavoritesCount() {
        return sqlDataBaseHandler.getFavoriteVenueCount();
    }

    public ArrayList<Venue> loadFavoriteVenue(int fromRecord, int limit) {
        return sqlDataBaseHandler.loadFavoriteVenue(fromRecord,limit);
    }

    public void deleteAllFavoriteVenues() {
        sqlDataBaseHandler.deleteAllFavoriteVenues();
    }
}
