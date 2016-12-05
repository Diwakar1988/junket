package com.github.diwakar1988.junket.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.diwakar1988.junket.R;
import com.github.diwakar1988.junket.ui.OnInputSaveListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

/**
 * Created by diwakar.mishra on 02/12/16.
 */
public class Utils {

    public static void call(Context context, String phoneNumber) {
        boolean hasPhone = context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_TELEPHONY);
        if (!hasPhone) {
            Toast.makeText(context.getApplicationContext(), "Your device doesn't supports calling feature!", Toast.LENGTH_SHORT).show();
            return;
        }

        //call car owner
        Intent callIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber));
        try {
            context.startActivity(callIntent);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(context.getApplicationContext(), "Calling application not found!", Toast.LENGTH_SHORT).show();
        }
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static boolean isLocationEnabled(Context context){
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && !locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            //All location services are disabled
            return false;
        }
        return true;
    }

}
