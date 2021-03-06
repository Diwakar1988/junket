package com.github.diwakar1988.junket.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import com.github.diwakar1988.junket.db.dao.FavoriteVenue;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "FAVORITE_VENUE".
*/
public class FavoriteVenueDao extends AbstractDao<FavoriteVenue, String> {

    public static final String TABLENAME = "FAVORITE_VENUE";

    /**
     * Properties of entity FavoriteVenue.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property VenueId = new Property(0, String.class, "venueId", true, "VENUE_ID");
        public final static Property Json = new Property(1, String.class, "json", false, "JSON");
        public final static Property Date = new Property(2, java.util.Date.class, "date", false, "DATE");
    };


    public FavoriteVenueDao(DaoConfig config) {
        super(config);
    }
    
    public FavoriteVenueDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"FAVORITE_VENUE\" (" + //
                "\"VENUE_ID\" TEXT PRIMARY KEY NOT NULL ," + // 0: venueId
                "\"JSON\" TEXT," + // 1: json
                "\"DATE\" INTEGER);"); // 2: date
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"FAVORITE_VENUE\"";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, FavoriteVenue entity) {
        stmt.clearBindings();
 
        String venueId = entity.getVenueId();
        if (venueId != null) {
            stmt.bindString(1, venueId);
        }
 
        String json = entity.getJson();
        if (json != null) {
            stmt.bindString(2, json);
        }
 
        java.util.Date date = entity.getDate();
        if (date != null) {
            stmt.bindLong(3, date.getTime());
        }
    }

    /** @inheritdoc */
    @Override
    public String readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public FavoriteVenue readEntity(Cursor cursor, int offset) {
        FavoriteVenue entity = new FavoriteVenue( //
            cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0), // venueId
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // json
            cursor.isNull(offset + 2) ? null : new java.util.Date(cursor.getLong(offset + 2)) // date
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, FavoriteVenue entity, int offset) {
        entity.setVenueId(cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0));
        entity.setJson(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setDate(cursor.isNull(offset + 2) ? null : new java.util.Date(cursor.getLong(offset + 2)));
     }
    
    /** @inheritdoc */
    @Override
    protected String updateKeyAfterInsert(FavoriteVenue entity, long rowId) {
        return entity.getVenueId();
    }
    
    /** @inheritdoc */
    @Override
    public String getKey(FavoriteVenue entity) {
        if(entity != null) {
            return entity.getVenueId();
        } else {
            return null;
        }
    }

    /** @inheritdoc */
    @Override    
    protected boolean isEntityUpdateable() {
        return true;
    }
    
}
