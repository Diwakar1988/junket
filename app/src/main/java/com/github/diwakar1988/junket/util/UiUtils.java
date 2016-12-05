package com.github.diwakar1988.junket.util;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
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

/**
 * Created by diwakar.mishra on 02/12/16.
 */

public class UiUtils {

    public static Snackbar showSnackBar(String msg,String btnText, View rootLayout, View.OnClickListener listener) {
        Snackbar snackbar = Snackbar.make(rootLayout, msg, Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction(btnText, listener);
        snackbar.setActionTextColor(Color.WHITE);
        snackbar.show();
        return snackbar;
    }

    public static Snackbar showSnackBar(String msg, View rootLayout, int time) {
        Snackbar snackbar = Snackbar.make(rootLayout, msg, time);
        snackbar.show();
        return snackbar;
    }
    public static void animateView(View view, final Runnable runnable) {
        Animation animation1 = new AlphaAnimation(0.3f, 1.0f);
        animation1.setDuration(100);
        animation1.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                runnable.run();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        view.startAnimation(animation1);
    }
    public static void showImageOnDialog(Activity context, String url) {

        Dialog dialog = new Dialog(context);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(context.getLayoutInflater().inflate(R.layout.image_layout
                , null));
        ImageView iv = (ImageView) dialog.findViewById(R.id.image_view);
        Picasso.with(context).load(url).into(iv);

        dialog.show();

    }
    public static Dialog showImageOnDialog(Activity context, String url, View.OnClickListener listener) {

        Dialog dialog = new Dialog(context);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(context.getLayoutInflater().inflate(R.layout.image_layout
                , null));
        ImageView iv = (ImageView) dialog.findViewById(R.id.image_view);
        iv.setOnClickListener(listener);
        Picasso.with(context).load(url).into(iv);

        dialog.show();
        return  dialog;

    }
    public static void showInputDialog(final Activity context, String title, String warning, final OnInputSaveListener listener) {


        final Dialog inputDialog = new Dialog(context);
        inputDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        inputDialog.setContentView(context.getLayoutInflater().inflate(R.layout.input_layout
                , null));
        TextView tvTitle = (TextView) inputDialog.findViewById(R.id.tv_title);
        TextView tvWarning = (TextView) inputDialog.findViewById(R.id.tv_warning);
        final EditText etInput = (EditText) inputDialog.findViewById(R.id.et_input);
        Button btnAdd = (Button) inputDialog.findViewById(R.id.btn_add);

        tvTitle.setText(title);
        tvWarning.setText(warning);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text=etInput.getText().toString();
                if (TextUtils.isEmpty(text)){
                    Toast.makeText(context, "Invalid text, try again!", Toast.LENGTH_SHORT).show();
                    return;
                }
                inputDialog.dismiss();
                listener.onInputSave(text);
            }
        });
        inputDialog.show();

    }

    public static void showConfirmation(Context context, String msg, String txtPositive, String txtNegative, final View.OnClickListener positiveListener) {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        positiveListener.onClick(null);
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(msg).setPositiveButton(txtPositive, dialogClickListener)
                .setNegativeButton(txtNegative, dialogClickListener).show();
    }


}
