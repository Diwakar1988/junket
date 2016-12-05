package com.github.diwakar1988.junket.gen;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;
/**
 * Created by diwakar.mishra on 02/12/16.
 */
public class MainGenerator {

    private static final String PROJECT_DIR = System.getProperty("user.dir");
    private static final int APP_VERSION = 1;

    public static void main(String[] args) {
        Schema schema = new Schema(APP_VERSION, "com.github.diwakar1988.junket.db.dao");
        schema.enableKeepSectionsByDefault();

        addTables(schema);

        try {
            new DaoGenerator().generateAll(schema, PROJECT_DIR + "/app/src/main/java");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void addTables(final Schema schema) {
        addReviewTable(schema);
        addFavoriteTable(schema);
    }

    private static Entity addFavoriteTable(Schema schema) {
        Entity entity = schema.addEntity("FavoriteVenue");
        entity.addStringProperty("venueId").primaryKey();
        entity.addStringProperty("json");
        entity.addDateProperty("date");
        return entity;
    }

    private static Entity addReviewTable(final Schema schema) {
        Entity entity = schema.addEntity("Review");
        entity.addStringProperty("venueId").primaryKey();
        entity.addStringProperty("text");
        entity.addDateProperty("date");
        return entity;
    }

}