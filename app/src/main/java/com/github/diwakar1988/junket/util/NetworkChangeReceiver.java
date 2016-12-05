package com.github.diwakar1988.junket.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.github.diwakar1988.junket.net.service.event.Event;
import com.github.diwakar1988.junket.net.service.event.EventManager;

/**
 * Created by diwakar.mishra on 05/12/16.
 */

public class NetworkChangeReceiver extends BroadcastReceiver {

    public NetworkChangeReceiver() {
    }

    @Override
    public void onReceive(final Context context, Intent intent) {
        if (Utils.isNetworkAvailable(context)) {
            //network is up we can try to reload venue is there is no venues
            EventManager.getInstance().broadcast(new Event(Event.TYPE_NETWORK_AVAILABLE));
        }
    }
}