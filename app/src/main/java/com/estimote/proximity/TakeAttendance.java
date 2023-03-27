package com.estimote.proximity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.estimote.coresdk.observation.region.beacon.BeaconRegion;
import com.estimote.coresdk.recognition.packets.Beacon;
import com.estimote.coresdk.service.BeaconManager;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class TakeAttendance extends AppCompatActivity {
    private BeaconManager beaconManager;
    private BeaconRegion regionBlueberry, regionIce, regionCoconut;
    private Trilateration trilateration;
    double[] result = new double[2];

    String tag = "login";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        //setting beacon
        beaconManager = new BeaconManager(this);

        EditText nameInput = findViewById(R.id.name_input);
        Button calculateButton = findViewById(R.id.calculate_button);
        TextView tvNameMsg = findViewById(R.id.tvNameMsg);
        TextView tvSResultX = findViewById(R.id.tvSResultX);
        TextView tvSResultY = findViewById(R.id.tvSResultY);
        TextView tvTimeMsg = findViewById(R.id.tvTimeMsg);
        Button btMap= findViewById(R.id.btMap);

        double[] x = new double[3];
        double[] y = new double[3];
        double[] rssi = new double[3];
        double[] txPower = new double[3];
        boolean[] states = new boolean[3];

        calculateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout login_layout = findViewById(R.id.login_layout);
                login_layout.setVisibility(View.VISIBLE);

                String name = nameInput.getText().toString();
                // Show loading indicator
                ProgressDialog progressDialog = ProgressDialog.show(TakeAttendance.this, "Calculating", "Please wait...", true);

                // Run calculation on a background thread
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... params) {

                        // Define the regions to search for the beacons
                        String regionNameBlueberry = "Blueberry";
                        UUID regionUUIDBlueberry = UUID.fromString("B9407F30-F5F8-466E-AFF9-25556B57FE6D");
                        int majorBlueberry = 10016;
                        int minorBlueberry = 15322;
                        regionBlueberry = new BeaconRegion(regionNameBlueberry, regionUUIDBlueberry, majorBlueberry, minorBlueberry);

                        String regionNameIce = "Ice";
                        UUID regionUUIDIce = UUID.fromString("B9407F30-F5F8-466E-AFF9-25556B57FE6D");
                        int majorIce = 20001;
                        int minorIce = 1;
                        regionIce = new BeaconRegion(regionNameIce, regionUUIDIce, majorIce, minorIce);

                        String regionNameCoconut = "Coconut";
                        UUID regionUUIDCoconut = UUID.fromString("B9407F30-F5F8-466E-AFF9-25556B57FE6D");
                        int majorCoconut = 30001;
                        int minorCoconut = 3;
                        regionCoconut = new BeaconRegion(regionNameCoconut, regionUUIDCoconut, majorCoconut, minorCoconut);

                        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
                            @Override
                            public void onServiceReady() {
                                beaconManager.startRanging(regionBlueberry);
                                beaconManager.startRanging(regionIce);
                                beaconManager.startRanging(regionCoconut);
                            }
                        });
                        return null;
                    }


                    protected void onPostExecute(double[] position) {
                        // Do nothing here
                    }
                }.execute();


                beaconManager.setRangingListener(new BeaconManager.BeaconRangingListener() {
                    @Override
                    public void onBeaconsDiscovered(BeaconRegion region, List<Beacon> beacons) {
                        Log.i("Beacon", "Start listen!");
                        // The result is a list of Beacon objects.
                        int i = 0;

                        for (Beacon beacon : beacons) {
                            // You get the UUID and the RSSI with:
                            Log.e("RSSI", "RSSI value: " + beacon.getRssi());
                            Log.e("power", "power value: " + beacon.getMeasuredPower());
                            // Now, call your own methods to work with the values.

                            // Store the position and distance of each beacon
                            switch (beacon.getMinor()) {
                                case 15322:
                                    x[0] = 1;
                                    y[0] = 15;
                                    rssi[0] = beacon.getRssi();
                                    txPower[0] = beacon.getMeasuredPower();
                                    Log.e("RSSI", "RSSI 15322 value: " + beacon.getRssi());
                                    states[0] = true;
                                    break;
                                case 1:
                                    x[1] = 15;
                                    y[1] = 15;
                                    rssi[1] = beacon.getRssi();
                                    txPower[1] = beacon.getMeasuredPower();
                                    Log.e("RSSI", "RSSI 1 value: " + beacon.getRssi());
                                    states[1] = true;
                                    break;
                                case 3:
                                    x[2] = 1;
                                    y[2] = 1;
                                    rssi[2] = beacon.getRssi();
                                    txPower[2] = beacon.getMeasuredPower();
                                    Log.e("RSSI", "RSSI 3 value: " + beacon.getRssi());
                                    states[2] = true;
                                    break;
                            }
                        }

                        Log.e("states", "states result: " + states[0] + ", " + states[1] + ", " + states[2]);

                        // Calculate the trilateration and store the result in the result array
                        if (states[0] != false && states[1] != false && states[2] != false) {
                            trilateration = new Trilateration();
                            result = trilateration.calculation(x, y, rssi, txPower);
                            Log.e("Location", "Location result: " + result[0] + ", " + result[1]);

                            // Update UI with the result
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    // Dismiss loading indicator
                                    progressDialog.dismiss();

                                    // Update UI with the result
                                    tvNameMsg.setText("Hello, " + nameInput.getText());
                                    tvSResultX.setText(Double.toString(result[0]));
                                    tvSResultY.setText(Double.toString(result[1]));
                                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                                    String currentDateTime = dateFormat.format(new Date());
                                    tvTimeMsg.setText("Recored time: " + currentDateTime);
                                }
                            });
                        }
                    }
                });
            }
        });

        btMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Map.class);
                intent.putExtra("result", result);
                intent.putExtra("x", x);
                intent.putExtra("y", y);
                intent.putExtra("test", 1);
                startActivityForResult(intent, 1);
            }
        });
    }
}
