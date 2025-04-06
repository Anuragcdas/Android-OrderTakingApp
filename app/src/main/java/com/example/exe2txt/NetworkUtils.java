package com.example.exe2txt;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.android.material.snackbar.Snackbar;

import java.nio.charset.StandardCharsets;

public class NetworkUtils {

    public static <T> void handleNetworkError(Context context, View rootView, VolleyError error, RepositoryCallback<T> callback) {
        String errorMessage = "Network Error! Please try again.";

        if (!isNetworkAvailable(context)) {
            errorMessage = "No Internet Connection!";
        } else if (error.networkResponse != null) {
            try {
                errorMessage = "Error " + error.networkResponse.statusCode + ": " +
                        new String(error.networkResponse.data, StandardCharsets.UTF_8);
            } catch (Exception e) {
                errorMessage = "Error " + error.networkResponse.statusCode + ": Unable to decode error message.";
            }
        } else if (error.getCause() != null) {
            errorMessage = "Error: " + error.getCause().getMessage();
        }

        // Persistent Snackbar until network is restored
        if (rootView != null) {
            Snackbar snackbar = Snackbar.make(rootView, errorMessage, Snackbar.LENGTH_INDEFINITE)
                    .setAction("Retry", v -> retryRequest(context, rootView, callback));
            snackbar.show();
        } else {
            Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show();
        }

        callback.onError(errorMessage);
    }

    private static <T> void retryRequest(Context context, View rootView, RepositoryCallback<T> callback) {
        if (isNetworkAvailable(context)) {
            Toast.makeText(context, "Retrying...", Toast.LENGTH_SHORT).show();
            new Handler(Looper.getMainLooper()).postDelayed(() -> callback.onSucess(null), 200);


        } else {
            // Retry Snackbar again until network is restored
            Snackbar snackbar = Snackbar.make(rootView, "Still No Internet!", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Retry", v -> retryRequest(context, rootView, callback));
            snackbar.show();
        }
    }


    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            Network activeNetwork = cm.getActiveNetwork();
            if (activeNetwork != null) {
                NetworkCapabilities networkCapabilities = cm.getNetworkCapabilities(activeNetwork);
                return networkCapabilities != null &&
                        (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET));
            }
        }
        return false;
    }
}
