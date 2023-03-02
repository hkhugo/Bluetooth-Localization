package com.estimote.proximity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.estimote.proximity.estimote.LeDeviceListAdapter;

public class MainActivity extends AppCompatActivity {
    DrawerLayout drawerLayout;
    TextView navigationTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);

        navigationTV = findViewById(R.id.navigationTV);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationTV.setText("Trilateration Algorithm calculator");
        loadFragment(new localizationFragment());
    }

    //Menu
    public void ClickMenu(View view) {
        openDrawer(drawerLayout);
    }

    //Localization function
    public void ClickHome(View view) {
        closeDrawer(drawerLayout);
        navigationTV.setText("Trilateration Algorithm calculator");
        loadFragment(new localizationFragment());
    }

    //Device List button function
    public void ClickListDevice(View view) {
        closeDrawer(drawerLayout);
        Intent intent;
        intent = new Intent(getApplicationContext(), ListDevice.class);
        startActivity(intent);
    }

    public void ClickLogout(View view) {
        logout(this);
    }

    public void ClickEmpty(View view) {
        closeDrawer(drawerLayout);
    }

    //logout button function
    public void logout(final Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Logout");
        builder.setMessage("Are you sure that you want to logout");
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                activity.finishAffinity();
            }
        });
        builder.show();
    }

    //
    public void onButtonClicked(View view) {
        String TAG = "MyActivity";
        Log.i(TAG, "Clicked");
        EditText ptTx = findViewById(R.id.ptTx);
        EditText ptx1 = findViewById(R.id.ptx1);
        EditText ptx2 = findViewById(R.id.ptx2);
        EditText ptx3 = findViewById(R.id.ptx3);
        EditText pty1 = findViewById(R.id.pty1);
        EditText pty2 = findViewById(R.id.pty2);
        EditText pty3 = findViewById(R.id.pty3);
        EditText ptRssi1 = findViewById(R.id.ptRSSI1);
        EditText ptRSSI2 = findViewById(R.id.ptRSSI2);
        EditText ptRSSI3 = findViewById(R.id.ptRSSI3);
//        EditText ptRssi = findViewById(R.id.ptRssi);
        TextView tvResultX = findViewById(R.id.tvResultX);
        TextView tvResultY = findViewById(R.id.tvResultY);

        double txPower = Double.parseDouble(ptTx.getText().toString());
        double[] result = new double[2];
        double[] x = new double[4];
        x[1] = Double.parseDouble(ptx1.getText().toString());
        x[2] = Double.parseDouble(ptx2.getText().toString());
        x[3] = Double.parseDouble(ptx3.getText().toString());
        double[] y = new double[4];
        y[1] = Double.parseDouble(pty1.getText().toString());
        y[2] = Double.parseDouble(pty2.getText().toString());
        y[3] = Double.parseDouble(pty3.getText().toString());
        double[] rssi = new double[4];
        rssi[1] = Double.parseDouble(ptRssi1.getText().toString());
        rssi[2] = Double.parseDouble(ptRSSI2.getText().toString());
        rssi[3] = Double.parseDouble(ptRSSI3.getText().toString());


        switch (view.getId()) {
            case R.id.btSubmit:
                FrameLayout container = findViewById(R.id.location_container);

                result = trilateration.calculation(x, y, rssi, txPower);
                Log.i(TAG, ("result is " + Double.toString(result[0])));
                Log.i(TAG, ("result is " + Double.toString(result[1])));
                tvResultX.setText(Double.toString(result[0]));
                tvResultY.setText(Double.toString(result[1]));

                // Get the location coordinates from the trilateration algorithm
                double rx = result[0];
                double ry = result[1];

                // Get the dimensions of the container
                int containerWidth = container.getWidth();
                int containerHeight = container.getHeight();

                // Find the max and min x and y values of the beacon coordinates
                double maxX = Math.max(Math.max(x[0], x[1]), x[2]);
                double maxY = Math.max(Math.max(y[0], y[1]), y[2]);
                double minX = Math.min(Math.min(x[0], x[1]), x[2]);
                double minY = Math.min(Math.min(y[0], y[1]), y[2]);

                // Calculate the range of the coordinates and the scaling factor
//                double rangeX = maxX - minX;
//                double rangeY = maxY - minY;
//                float scaleX = (float) containerWidth / (float) rangeX;
//                float scaleY = (float) containerHeight / (float) rangeY;

                // Calculate the scaling factor based on the range of the beacon coordinates
                double scaleFactor = Math.min((containerWidth - 20) / (maxX - minX), (containerHeight - 20) / (maxY - minY));

                // Create the LocationViews with the scaled coordinates
                LocationView locationView = new LocationView(this, (float) ((rx - minX) * scaleFactor), (float) ((ry - minY) * scaleFactor), Color.BLACK);
                LocationView locationView1 = new LocationView(this, (float) ((x[0] - minX) * scaleFactor) + 10, (float) ((y[0] - minY) * scaleFactor) + 10, Color.RED);
                LocationView locationView2 = new LocationView(this, (float) ((x[1] - minX) * scaleFactor) + 10, (float) ((y[1] - minY) * scaleFactor) + 10, Color.GREEN);
                LocationView locationView3 = new LocationView(this, (float) ((x[2] - minX) * scaleFactor) + 10, (float) ((y[2] - minY) * scaleFactor) + 10, Color.BLUE);

                // Find the location_container and add the LocationView to it

                container.removeAllViews();
                container.addView(locationView);
                container.addView(locationView1);
                container.addView(locationView2);
                container.addView(locationView3);

                break;
            default:
                throw new IllegalStateException("Unexpected value: " + view.getId());
        }

    }


    private void openDrawer(DrawerLayout drawerLayout) {
        drawerLayout.openDrawer(GravityCompat.START);
    }


    private void closeDrawer(DrawerLayout drawerLayout) {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    private void loadFragment(Fragment fragment) {
        //switching fragment
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent e) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            logout(this);
            return true;
        } else
            return super.onKeyDown(keyCode, e);
    }
}
