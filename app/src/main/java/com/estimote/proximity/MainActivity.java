package com.estimote.proximity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.estimote.coresdk.common.requirements.SystemRequirementsChecker;
import com.estimote.coresdk.observation.region.beacon.BeaconRegion;
import com.estimote.coresdk.service.BeaconManager;
import com.estimote.proximity.algorithms.Trilateration;
import com.estimote.proximity.deviceList.ListDevice;
import com.estimote.proximity.map.Map;
//import com.estimote.proximity.tensorFlowLite.BeaconCNN;
//import com.estimote.proximity.tensorFlowLite.BeaconFFNN;
import com.estimote.proximity.tensorFlowLite.BeaconLocalizer;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements PredictionListener {
    DrawerLayout drawerLayout;
    TextView navigationTV;

    private BeaconManager beaconManager;
    private BeaconRegion region;

    private int beaconNumber = 0;
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private static final int NEARBY_DEVICES = 2;
    TextView tvResultX;
    TextView tvResultY;

    PredictionListener predictionListener = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        beaconManager = new BeaconManager(getApplicationContext());

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);

        predictionListener = this;

        tvResultX = findViewById(R.id.tvResultX);
        tvResultY = findViewById(R.id.tvResultY);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, NEARBY_DEVICES);
        }

        navigationTV = findViewById(R.id.navigationTV);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationTV.setText("Algorithm calculator");
        loadFragment(new localizationFragment());
    }

    //Menu
    public void ClickMenu(View view) {
        openDrawer(drawerLayout);
    }

    //Localization function
    public void ClickHome(View view) {
        closeDrawer(drawerLayout);
        navigationTV.setText("Algorithm calculator");
        loadFragment(new localizationFragment());
    }

    //Device List button function
    public void ClickListDevice(View view) {
        closeDrawer(drawerLayout);
        Intent intent;
        intent = new Intent(getApplicationContext(), ListDevice.class);
        startActivity(intent);
    }

    //Device Take attendance button function
    public void ClickLogin(View view) {
        closeDrawer(drawerLayout);
        Intent intent;
        intent = new Intent(getApplicationContext(), TakeAttendance.class);
        startActivity(intent);
    }

    //logout
    public void ClickLogout(View view) throws Exception {
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

    //summit calculation
    public void onButtonClicked(View view) {
        String TAG = "MyActivity";
        Log.i(TAG, "Clicked calculate");
        EditText ptx1 = findViewById(R.id.ptx1);
        EditText ptx2 = findViewById(R.id.ptx2);
        EditText ptx3 = findViewById(R.id.ptx3);
        EditText ptx4 = findViewById(R.id.ptx4);
        EditText pty1 = findViewById(R.id.pty1);
        EditText pty2 = findViewById(R.id.pty2);
        EditText pty3 = findViewById(R.id.pty3);
        EditText pty4 = findViewById(R.id.pty4);
        EditText ptRssi1 = findViewById(R.id.ptRSSI1);
        EditText ptRSSI2 = findViewById(R.id.ptRSSI2);
        EditText ptRSSI3 = findViewById(R.id.ptRSSI3);
        EditText ptRSSI4 = findViewById(R.id.ptRSSI4);
        EditText ptTx1 = findViewById(R.id.ptTx1);
        EditText ptTx2 = findViewById(R.id.ptTx2);
        EditText ptTx3 = findViewById(R.id.ptTx3);
        EditText ptTx4 = findViewById(R.id.ptTx4);
        tvResultX = findViewById(R.id.tvResultX);
        tvResultY = findViewById(R.id.tvResultY);

        double[] txPower;
        double[] result = new double[2];
        double[] x;
        double[] y;
        double[] rssi;

        //Trilateration in 3 Beacon
        switch (view.getId()) {
            case R.id.btSubmit:
                beaconNumber = 3;
                txPower = new double[3];
                txPower[0] = Double.parseDouble(ptTx1.getText().toString());
                txPower[1] = Double.parseDouble(ptTx2.getText().toString());
                txPower[2] = Double.parseDouble(ptTx3.getText().toString());
                x = new double[3];
                x[0] = Double.parseDouble(ptx1.getText().toString());
                x[1] = Double.parseDouble(ptx2.getText().toString());
                x[2] = Double.parseDouble(ptx3.getText().toString());
                y = new double[3];
                y[0] = Double.parseDouble(pty1.getText().toString());
                y[1] = Double.parseDouble(pty2.getText().toString());
                y[2] = Double.parseDouble(pty3.getText().toString());
                rssi = new double[3];
                rssi[0] = Double.parseDouble(ptRssi1.getText().toString());
                rssi[1] = Double.parseDouble(ptRSSI2.getText().toString());
                rssi[2] = Double.parseDouble(ptRSSI3.getText().toString());

                result = Trilateration.calculation(x, y, rssi, txPower);
                tvResultX.setText(Double.toString(result[0]));
                tvResultY.setText(Double.toString(result[1]));
                break;

            //Trilateration in 4 Beacon
            case R.id.btSubmitExtraBeacon:
                beaconNumber = 4;
                txPower = new double[4];
                txPower[0] = Double.parseDouble(ptTx1.getText().toString());
                txPower[1] = Double.parseDouble(ptTx2.getText().toString());
                txPower[2] = Double.parseDouble(ptTx3.getText().toString());
                txPower[3] = Double.parseDouble(ptTx4.getText().toString());
                x = new double[4];
                x[0] = Double.parseDouble(ptx1.getText().toString());
                x[1] = Double.parseDouble(ptx2.getText().toString());
                x[2] = Double.parseDouble(ptx3.getText().toString());
                x[3] = Double.parseDouble(ptx4.getText().toString());
                y = new double[4];
                y[0] = Double.parseDouble(pty1.getText().toString());
                y[1] = Double.parseDouble(pty2.getText().toString());
                y[2] = Double.parseDouble(pty3.getText().toString());
                y[3] = Double.parseDouble(pty4.getText().toString());
                rssi = new double[4];
                rssi[0] = Double.parseDouble(ptRssi1.getText().toString());
                rssi[1] = Double.parseDouble(ptRSSI2.getText().toString());
                rssi[2] = Double.parseDouble(ptRSSI3.getText().toString());
                rssi[3] = Double.parseDouble(ptRSSI4.getText().toString());
                result = Trilateration.multiCalculation(x, y, rssi, txPower);
                tvResultX.setText(Double.toString(result[0]));
                tvResultY.setText(Double.toString(result[1]));
                break;

            //CNN
            case R.id.btCNN:
                beaconNumber = 4;
                txPower = new double[4];
                txPower[0] = Double.parseDouble(ptTx1.getText().toString());
                txPower[1] = Double.parseDouble(ptTx2.getText().toString());
                txPower[2] = Double.parseDouble(ptTx3.getText().toString());
                txPower[3] = Double.parseDouble(ptTx4.getText().toString());
                x = new double[4];
                x[0] = Double.parseDouble(ptx1.getText().toString());
                x[1] = Double.parseDouble(ptx2.getText().toString());
                x[2] = Double.parseDouble(ptx3.getText().toString());
                x[3] = Double.parseDouble(ptx4.getText().toString());
                y = new double[4];
                y[0] = Double.parseDouble(pty1.getText().toString());
                y[1] = Double.parseDouble(pty2.getText().toString());
                y[2] = Double.parseDouble(pty3.getText().toString());
                y[3] = Double.parseDouble(pty4.getText().toString());
                rssi = new double[4];
                rssi[0] = Double.parseDouble(ptRssi1.getText().toString());
                rssi[1] = Double.parseDouble(ptRSSI2.getText().toString());
                rssi[2] = Double.parseDouble(ptRSSI3.getText().toString());
                rssi[3] = Double.parseDouble(ptRSSI4.getText().toString());
                BeaconLocalizer beaconLocalizer = null;
                try {
                    beaconLocalizer = new BeaconLocalizer(this);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                result = beaconLocalizer.calculatePosition(rssi);
                tvResultX.setText(Double.toString(result[0]));
                tvResultY.setText(Double.toString(result[1]));
                break;

            //random forest
            case R.id.btRF:
                beaconNumber = 4;
                rssi = new double[4];
                rssi[0] = Double.parseDouble(ptRssi1.getText().toString());
                rssi[1] = Double.parseDouble(ptRSSI2.getText().toString());
                rssi[2] = Double.parseDouble(ptRSSI3.getText().toString());
                rssi[3] = Double.parseDouble(ptRSSI4.getText().toString());
                RF rf = new RF(predictionListener, rssi);
                rf.execute();
                break;
                //map button
            case R.id.btShowmap:
                Log.i(TAG, "clicked map now");
                if (result != null) {
                    Intent intent;
                    intent = new Intent(getApplicationContext(), Map.class);
                    intent.putExtra("result", result);
                    result[0] = Double.parseDouble(tvResultX.getText().toString());
                    result[1] = Double.parseDouble(tvResultY.getText().toString());
                    Log.i(TAG, "beaconNumber: " + beaconNumber);
                    if (beaconNumber > 3) {
                        x = new double[4];
                        x[0] = Double.parseDouble(ptx1.getText().toString());
                        x[1] = Double.parseDouble(ptx2.getText().toString());
                        x[2] = Double.parseDouble(ptx3.getText().toString());
                        x[3] = Double.parseDouble(ptx4.getText().toString());
                        y = new double[4];
                        y[0] = Double.parseDouble(pty1.getText().toString());
                        y[1] = Double.parseDouble(pty2.getText().toString());
                        y[2] = Double.parseDouble(pty3.getText().toString());
                        y[3] = Double.parseDouble(pty4.getText().toString());
                        intent.putExtra("test", 2);
                    } else {
                        x = new double[3];
                        x[0] = Double.parseDouble(ptx1.getText().toString());
                        x[1] = Double.parseDouble(ptx2.getText().toString());
                        x[2] = Double.parseDouble(ptx3.getText().toString());
                        y = new double[3];
                        y[0] = Double.parseDouble(pty1.getText().toString());
                        y[1] = Double.parseDouble(pty2.getText().toString());
                        y[2] = Double.parseDouble(pty3.getText().toString());
                        intent.putExtra("test", 1);
                    }
                    intent.putExtra("x", x);
                    intent.putExtra("y", y);

                    startActivity(intent);
                }
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

    @Override
    protected void onResume() {
        super.onResume();
        SystemRequirementsChecker.checkWithDefaultDialogs(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        beaconManager.disconnect();
    }

    @Override
    public void onPredictionReceived(double[] position) {
        Log.e("Location", "Location result(RF with 4 beacon RSSI): " + position[0] + " " + position[1]);
        tvResultX.setText("" + position[0]);
        tvResultY.setText("" + position[1]);

    }
}
