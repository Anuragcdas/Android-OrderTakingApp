package com.example.exe2txt;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import com.google.android.material.appbar.MaterialToolbar;

public class AppConstants {


    public static void showConfirmationDialog(Context context, String title, String message,
                                              ConfirmationDialogListener listener) {
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Yes", (dialog, which) -> {
                    if (listener != null) {
                        listener.onConfirmed();  // Call the onConfirmed method if user clicks Yes
                    }
                })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
    }

    public static void setupToolbarWithBack(final Activity activity, MaterialToolbar toolbar) {
        if (activity instanceof AppCompatActivity) {
            AppCompatActivity appCompatActivity = (AppCompatActivity) activity;
            appCompatActivity.setSupportActionBar(toolbar);

            if (appCompatActivity.getSupportActionBar() != null) {
                appCompatActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

                // Load and resize the back arrow icon programmatically


                Drawable navIcon = ContextCompat.getDrawable(activity, R.drawable.arrowback);

                if (navIcon != null) {
                    navIcon = DrawableCompat.wrap(navIcon).mutate(); // Ensure it's mutable
                    navIcon.setTint(ContextCompat.getColor(activity, android.R.color.white));

                    int width = (int) activity.getResources().getDimension(R.dimen.icon_small_width);
                    int height = (int) activity.getResources().getDimension(R.dimen.icon_small_height);

                    Log.d("Toolbar", "Width: " + width + " Height: " + height);  // Debugging

                    Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                    Canvas canvas = new Canvas(bitmap);
                    navIcon.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                    navIcon.draw(canvas);

                    toolbar.setNavigationIcon(new BitmapDrawable(activity.getResources(), bitmap)); // Set the new resized bitmap
                }

                // Handle toolbar back button click
                toolbar.setNavigationOnClickListener(v -> handleBackPress(activity));
            }

            // Properly handle back press
            appCompatActivity.getOnBackPressedDispatcher().addCallback(appCompatActivity, new OnBackPressedCallback(true) {
                @Override
                public void handleOnBackPressed() {
                    handleBackPress(appCompatActivity);
                }
            });
        }
    }

    // Handle back press to finish activity
    private static void handleBackPress(Activity activity) {
        activity.finish();
    }

    // Interface to handle confirmation actions
    public interface ConfirmationDialogListener {
        void onConfirmed();
    }


}
