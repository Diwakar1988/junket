package com.github.diwakar1988.junket.db;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;

/**
 * Created by diwakar.mishra on 02/12/16.
 */

public class AppPreferences {
    private static final String TAG = AppPreferences.class.getSimpleName();
    private static final String NAME = "pref_junket";
    private static AppPreferences _instance;

    protected static final String KEY_SETTINGS = "settings";


    private SharedPreferences preferences;

    private AppPreferences(Context context) {
        preferences = context.getSharedPreferences(
                NAME, Context.MODE_PRIVATE);
    }
    static synchronized void init(Context context){
        _instance=new AppPreferences(context);
    }

    static AppPreferences getInstance() {
        if (_instance == null) {
            throw new IllegalStateException("_instance is NULL, please call init() method before.");
        }
        return _instance;
    }


    /**
     * This Method Clear shared preference.
     */
    protected void clear() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();
    }

    protected void save() {
        preferences.edit().apply();
    }

    private void setString(String key, String value) {
        if (key != null && value != null) {
            try {
                if (preferences != null) {
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString(key, value);
                    editor.commit();
                }
            } catch (Exception e) {
                Log.e(TAG, "Unable to set " + key + "= " + value
                        + "in shared preference", e);
            }
        }
    }


    private String getString(String key, String defaultValue) {
        if (preferences != null && key != null && preferences.contains(key)) {
            return preferences.getString(key, defaultValue);
        }
        return defaultValue;
    }

    void saveSettings(Settings settings){
        if (settings==null){
            return;
        }
        String str = new Gson().toJson(settings,Settings.class);
        setString(KEY_SETTINGS,str);
    }
    Settings loadSettings(){
        String str = getString(KEY_SETTINGS,"");
        Settings settings=null;
        if (TextUtils.isEmpty(str)){
            settings=new Settings();
        }else{
            settings = new Gson().fromJson(str,Settings.class);
        }
        return settings;
    }
}
