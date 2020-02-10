package com.techcloud.isecurity.helpers;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.techcloud.isecurity.R;
import com.techcloud.isecurity.models.Guard;

import org.json.JSONObject;

import java.io.IOException;

import retrofit2.HttpException;

public class HolderClass {

    public static Bitmap theBitmap;

    public static Guard guard;

    public static String user;

    /**
     * Showing a Snackbar with error message
     * The error body will be in json format
     * {"error": "Error message!"}
     */
    public static void showError(Throwable e, View theView) {
        String message = "";
        try {
            if (e instanceof InputException || e instanceof ServerException) {
                message = e.getMessage();
            } else if (e instanceof IOException) {
                message = "Error! No internet connection.";
            } else if (e instanceof HttpException) {
                HttpException error = (HttpException) e;
                String errorBody = error.response().errorBody().string();
                JSONObject jObj = new JSONObject(errorBody);

                message = jObj.getString("error");
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        }

        if (TextUtils.isEmpty(message)) {
            message = "Unknown error occurred! Please consult system admin.";
        }

        Snackbar snackbar = (Snackbar) Snackbar
                .make(theView, message, Snackbar.LENGTH_LONG);
        View sbView = snackbar.getView();
        TextView textView = sbView.findViewById(R.id.snackbar_text);
        textView.setTextColor(Color.YELLOW);
        snackbar.show();
    }
}
