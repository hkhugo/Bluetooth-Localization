package com.estimote.proximity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ZoomControls;

import androidx.appcompat.app.AppCompatActivity;

public class Map extends AppCompatActivity {

    double[] result, x, y;
    double rx, ry;
    int test;

    //store information about zoomControls
    private ZoomControls zoomControls;
    private float currentScale = 1.0f;
    private static final float MIN_SCALE = 0.8f;
    private static final float MAX_SCALE = 2.0f;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map);


        FrameLayout container = findViewById(R.id.location_container);
        zoomControls = findViewById(R.id.zoom_controls);

        zoomControls.setOnZoomInClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentScale < MAX_SCALE) {
                    currentScale += 0.1f;
                    container.setScaleX(currentScale);
                    container.setScaleY(currentScale);
                }
            }
        });

        zoomControls.setOnZoomOutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentScale > MIN_SCALE) {
                    currentScale -= 0.1f;
                    container.setScaleX(currentScale);
                    container.setScaleY(currentScale);
                }
            }
        });

        container.setOnTouchListener(new View.OnTouchListener() {
            float startX, startY;
            float translationX, translationY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startX = event.getX();
                        startY = event.getY();
                        translationX = container.getTranslationX();
                        translationY = container.getTranslationY();
                        break;

                    case MotionEvent.ACTION_MOVE:
                        float dx = event.getX() - startX;
                        float dy = event.getY() - startY;
                        container.setTranslationX(translationX + dx);
                        container.setTranslationY(translationY + dy);
                        break;
                }
                return true;
            }
        });


        String TAG = "map";
        Log.i(TAG, "in map now");

        // Get the location coordinates from the trilateration algorithm
        Intent intent = getIntent();
        if (intent != null) {

            result = intent.getDoubleArrayExtra("result");
            x = intent.getDoubleArrayExtra("x");
            y = intent.getDoubleArrayExtra("y");
            test = intent.getIntExtra("test", 0);
            Log.i(TAG, "test: " + test );

            rx = result[0];
            ry = result[1];

            Log.i(TAG, "rx is " + rx);
            Log.i(TAG, "ry is " + ry);


            // Get the dimensions of the container
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            double containerWidth = (displayMetrics.widthPixels * 0.9);
            double containerHeight = (displayMetrics.heightPixels * 0.9);
            Log.i(TAG, ("containerWidth is " + Double.toString(containerHeight)));
            Log.i(TAG, ("containerHeight is " + Double.toString(containerHeight)));

            if (test == 1) {

                // Find the max and min x and y values of the beacon coordinates
                double maxX = Math.max(rx, Math.max(Math.max(x[0], x[1]), x[2]));
                double maxY = Math.max(ry, Math.max(Math.max(y[0], y[1]), y[2]));
                double minX = Math.min(rx, Math.min(Math.min(x[0], x[1]), x[2]));
                double minY = Math.min(ry, Math.min(Math.min(y[0], y[1]), y[2]));

                // Set a padding value (in pixels) for the edges of the screen
                int padding = 80;

                // Calculate the range of the coordinates and the scaling factor
                double rangeX = maxX - minX;
                double rangeY = maxY - minY;
                float scaleX = (float) (containerWidth - 2 * padding) / (float) rangeX;
                float scaleY = (float) (containerHeight - 2 * padding) / (float) rangeY;

                // Create a LocationView object for the current device's location
                LocationView locationView = new LocationView(this,
                        (float) (((rx - minX) * scaleX + padding) * 0.9),
                        (float) (((ry - minY) * scaleY + padding) * 0.9),
                        Color.BLACK,
                        " Beacon 1: (" + x[0] + ", " + y[0] + ")",
                        16);

                // Create a LocationView object for device 1's location
                LocationView locationView1 = new LocationView(this,
                        (float) (((x[0] - minX) * scaleX + padding) * 0.9),
                        (float) (((y[0] - minY) * scaleY + padding) * 0.9),
                        Color.RED,
                        " Beacon 2: (" + x[1] + ", " + y[1] + ")",
                        16);

                // Create a LocationView object for device 2's location
                LocationView locationView2 = new LocationView(this,
                        (float) (((x[1] - minX) * scaleX + padding) * 0.9),
                        (float) (((y[1] - minY) * scaleY + padding) * 0.9),
                        Color.GREEN,
                        " Beacon 3: (" + x[2] + ", " + y[2] + ")",
                        16);

                // Create a LocationView object for device 3's location
                LocationView locationView3 = new LocationView(this,
                        (float) (((x[2] - minX) * scaleX + padding) * 0.9),
                        (float) (((y[2] - minY) * scaleY + padding) * 0.9),
                        Color.BLUE,
                        " target device: (" + rx + ", " + ry + ")",
                        16);

                // Find the location_container and add the LocationView to it

                container.removeAllViews();
                container.addView(locationView);
                container.addView(locationView1);
                container.addView(locationView2);
                container.addView(locationView3);
            }
            else{



            }

        }
    }
}
