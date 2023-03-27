package com.estimote.proximity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ZoomControls;

import androidx.appcompat.app.AppCompatActivity;

public class Map extends AppCompatActivity {

    private Handler mHandler;
    private Runnable mRunnable;

    double[] result, x, y;
    double rx, ry;
    int test;

    String TAG = "map";

    //store information about zoomControls
    private ZoomControls zoomControls;
    private float currentScale = 1.0f;
    private static final float MIN_SCALE = 0.8f;
    private static final float MAX_SCALE = 2.0f;

    float scaleX;
    float scaleY;
    double rangeX;
    double rangeY;
    double maxX;
    double maxY;
    double minX;
    double minY;
    // Set a padding value (in pixels) for the edges of the screen
    int padding = 80;

    FrameLayout container;

    LocationView locationView;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map);

        zoomControls = findViewById(R.id.zoom_controls);
        container = findViewById(R.id.location_container);

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
                maxX = Math.max(rx, Math.max(Math.max(x[0], x[1]), x[2]));
                maxY = Math.max(ry, Math.max(Math.max(y[0], y[1]), y[2]));
                minX = Math.min(rx, Math.min(Math.min(x[0], x[1]), x[2]));
                minY = Math.min(ry, Math.min(Math.min(y[0], y[1]), y[2]));

                // Calculate the range of the coordinates and the scaling factor
                rangeX = maxX - minX;
                rangeY = maxY - minY;
                scaleX = (float) (containerWidth - 2 * padding) / (float) rangeX;
                scaleY = (float) (containerHeight - 2 * padding) / (float) rangeY;

                // Create a LocationView object for the current device's location
                locationView = new LocationView(this,
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

//        mHandler = new Handler();
//        mRunnable = new Runnable() {
//            @Override
//            public void run() {
//                // Get the updated data here
//                // For example, you can get the data from a static variable in the other activity
//                TakeAttendance takeAttendance = new TakeAttendance();
//                Intent intent = getIntent();
//                result = intent.getDoubleArrayExtra("result");
//
//                updateMap(result);
//
//                // Schedule the next update
//                mHandler.postDelayed(this, 1000); // 1000 milliseconds = 1 second
//            }
//        };
    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//        mHandler.postDelayed(mRunnable, 1000); // Start the first update after 1 second
//    }
//
//    void updateMap(double[] result){
//
//        Log.i(TAG, "updateMap");
//        locationView.setmX((float) (((result[0] - minX) * scaleX + padding) * 0.9));
//        locationView.setmY((float) (((result[1] - minY) * scaleY + padding) * 0.9));
//        locationView.setmLabel(" target device: (" + result[0] + ", " + result[1] + ")");
//        Log.i(TAG, " target device: (" + result[0] + ", " + result[1] + ")");
//        container.invalidate();
//    }
}
