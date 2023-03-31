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
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.estimote.coresdk.observation.region.beacon.BeaconRegion;
import com.estimote.coresdk.recognition.packets.Beacon;
import com.estimote.coresdk.service.BeaconManager;
import com.estimote.proximity.algorithms.Trilateration;
import com.estimote.proximity.map.Map;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class TakeAttendance extends AppCompatActivity {
    private BeaconManager beaconManager;
    private BeaconRegion regionBlueberry, regionMint, regionCoconut, regionIce;
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
        Button btMap = findViewById(R.id.btMap);

        double[] x = new double[4];
        double[] y = new double[4];
        double[] rssi = new double[4];
        double[] recordedRssi = new double[4];
        int[] recordTime = {0, 0, 0};
        double[] txPower = new double[4];
        double[] recordedTxpower = new double[4];
        boolean[] states = new boolean[4];

        calculateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Capture the start time
                final long startTime = System.currentTimeMillis();

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

                        String regionNameMint = "mint";
                        UUID regionUUIDMint = UUID.fromString("B9407F30-F5F8-466E-AFF9-25556B57FE6D");
                        int majorMint = 30001;
                        int minorMint = 2;
                        regionMint = new BeaconRegion(regionNameMint, regionUUIDMint, majorMint, minorMint);

                        String regionNameCoconut = "Coconut";
                        UUID regionUUIDCoconut = UUID.fromString("B9407F30-F5F8-466E-AFF9-25556B57FE6D");
                        int majorCoconut = 30001;
                        int minorCoconut = 3;
                        regionCoconut = new BeaconRegion(regionNameCoconut, regionUUIDCoconut, majorCoconut, minorCoconut);

                        String regionNameIce = "Ice";
                        UUID regionUUIDIce = UUID.fromString("B9407F30-F5F8-466E-AFF9-25556B57FE6D");
                        int majorIce = 30001;
                        int minorIce = 1;
                        regionIce = new BeaconRegion(regionNameIce, regionUUIDIce, majorIce, minorIce);

                        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
                            @Override
                            public void onServiceReady() {
                                beaconManager.startRanging(regionIce);
                                beaconManager.startRanging(regionBlueberry);
                                beaconManager.startRanging(regionMint);
                                beaconManager.startRanging(regionCoconut);
                            }
                        });
                        return null;
                    }


                    protected void onPostExecute(double[] position) {
                    }
                }.execute();

                beaconManager.setRangingListener(new BeaconManager.BeaconRangingListener() {
                    @Override
                    public void onBeaconsDiscovered(BeaconRegion region, List<Beacon> beacons) {
                        Log.i("Beacon", "Start listen!");
                        // The result is a list of Beacon objects.
                        int i = 0;

                        for (Beacon beacon : beacons) {

                            // Store the position and distance of each beacon
                            switch (beacon.getMinor()) {

                                case 15322:
                                    //blueberry
                                    x[0] = 400;
                                    y[0] = 0;
                                    if (recordTime[0] >= 3) {
                                        states[0] = true;
                                    } else {
                                        recordedRssi[0] = recordedRssi[0] + beacon.getRssi();
                                        recordedTxpower[0] = recordedTxpower[0] + beacon.getMeasuredPower();
                                        Log.e("RSSI", "RSSI 15322 value: " + beacon.getRssi());
                                        recordTime[0]++;
                                    }
                                    break;
                                case 2:
                                    //ice
                                    x[1] = 46;
                                    y[1] = 532;
                                    if (recordTime[1] >= 3) {
                                        states[1] = true;
                                    } else {
                                        recordedRssi[1] = recordedRssi[1] + beacon.getRssi();
                                        recordedTxpower[1] = recordedTxpower[0] + beacon.getMeasuredPower();
                                        Log.e("RSSI", "RSSI 2 value: " + beacon.getRssi());
                                        recordTime[1]++;
                                    }
                                    break;
                                case 3:
                                    //coconut
                                    x[2] = 835;
                                    y[2] = 554;
                                    if (recordTime[2] >= 3) {
                                        states[2] = true;
                                    } else {
                                        recordedRssi[2] = recordedRssi[2] + beacon.getRssi();
                                        recordedTxpower[2] = recordedTxpower[2] + beacon.getMeasuredPower();
                                        Log.e("RSSI", "RSSI 3 value: " + beacon.getRssi());
                                        recordTime[2]++;
                                    }
                                    break;

                                case 1:
                                    //coconut
                                    x[3] = 835;
                                    y[3] = 554;
                                    if (recordTime[3] >= 3) {
                                        states[3] = true;
                                    } else {
                                        recordedRssi[3] = recordedRssi[3] + beacon.getRssi();
                                        recordedTxpower[3] = recordedTxpower[3] + beacon.getMeasuredPower();
                                        Log.e("RSSI", "RSSI 1 value: " + beacon.getRssi());
                                        recordTime[3]++;
                                    }
                                    break;
                            }
                        }

                        Log.e("states", "states result: blueberry:" + states[0] + ", mint:" + states[1] + ", coconut:" + states[2]);

                        // Calculate the trilateration and store the result in the result array
                        if (states[0] != false && states[1] != false && states[2] != false) {
                            for (int j = 0; j < 3; j++) {
                                rssi[j] = recordedRssi[j] / recordTime[j];
                                txPower[j] = recordedTxpower[j] / recordTime[j];
                                Log.e("RSSI", "Average RSSI value of beacon" + j + " : " + rssi[j]);
                            }
                            trilateration = new Trilateration();
                            result = trilateration.calculation(x, y, rssi, txPower);
                            Log.e("Location", "Location result: " + result[0] + ", " + result[1]);

                            // Update UI with the result
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    // Update UI with the result
                                    tvNameMsg.setText("Hello, " + nameInput.getText());
                                    tvSResultX.setText(Double.toString(result[0]));
                                    tvSResultY.setText(Double.toString(result[1]));
                                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                                    String currentDateTime = dateFormat.format(new Date());
                                    tvTimeMsg.setText("Recored time: " + currentDateTime);

                                    long elapsedTime = System.currentTimeMillis() - startTime;

                                    // Update the UI with the elapsed time
                                    Toast.makeText(TakeAttendance.this, "Calculations completed in " + elapsedTime + " ms", Toast.LENGTH_SHORT).show();
                                    // Hide the loading indicator
                                    progressDialog.dismiss();
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
