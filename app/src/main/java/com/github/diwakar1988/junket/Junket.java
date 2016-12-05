package com.github.diwakar1988.junket;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import com.github.diwakar1988.junket.db.DataController;
import com.github.diwakar1988.junket.net.service.ServiceController;

/**
 * Created by diwakar.mishra on 02/12/16.
 */

public class Junket extends Application {
    private static Junket instance;
    private Handler handler;
    @Override
    public void onCreate() {
        super.onCreate();
        instance=this;

        //Init handler for UI posts
        handler = new Handler(Looper.getMainLooper());

        //initialize DataController class
        DataController.init();

        //initialize HTTP ServiceController
        ServiceController.init();
    }

    public static Junket getInstance() {
        return instance;
    }

    public void runOnUiThread(Runnable runnable) {
            handler.post(runnable);
    }

}
