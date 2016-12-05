package com.github.diwakar1988.junket.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.github.diwakar1988.junket.db.dao.DaoMaster;
import com.github.diwakar1988.junket.db.dao.DaoSession;
import com.github.diwakar1988.junket.db.dao.FavoriteVenue;
import com.github.diwakar1988.junket.db.dao.Review;
import com.github.diwakar1988.junket.pojo.Venue;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by diwakar.mishra on 02/12/16.
 */
public class SQLDataBaseHandler {

    private static final String DB_NAME = "junket_db";
    private DaoSession session;
    private static final Gson GSON = new Gson();
    SQLDataBaseHandler(Context context) {
        initDao(context);
    }

    private void initDao(Context context) {
        DaoMaster.DevOpenHelper openHelper = new DaoMaster.DevOpenHelper(context, DB_NAME, null);
        SQLiteDatabase db = openHelper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        session = daoMaster.newSession();
    }

    void saveReview(Review review) {

        session.getReviewDao().insertOrReplace(review);

    }

    Review getReview(String venueId){
        return session.getReviewDao().load(venueId);
    }

    FavoriteVenue getFavoriteVenue(String venueId){
        return session.getFavoriteVenueDao().load(venueId);
    }

    void deleteFavoriteVenue(String venueId) {
        session.getFavoriteVenueDao().deleteByKey(venueId);
    }

    void saveFavoriteVenue(Venue venue) {
        FavoriteVenue favoriteVenue = new FavoriteVenue();
        favoriteVenue.setVenueId(venue.id);
        favoriteVenue.setJson(GSON.toJson(venue,Venue.class));
        favoriteVenue.setDate(new Date());
        session.getFavoriteVenueDao().insertOrReplace(favoriteVenue);
    }

    ArrayList<Venue> loadFavoriteVenue(int fromRecord, int limit) {
        ArrayList<Venue> list = new ArrayList<>();

        List<FavoriteVenue> venues = session.getFavoriteVenueDao().queryBuilder().offset(fromRecord).limit(limit).build().list();
        for (int i = 0; i < venues.size(); i++) {
            list.add(GSON.fromJson(venues.get(i).getJson(),Venue.class));
        }
        return list;
    }

    void deleteAllFavoriteVenues() {
        session.getFavoriteVenueDao().deleteAll();
    }

    long getFavoriteVenueCount() {
        return session.getFavoriteVenueDao().count();
    }
}
