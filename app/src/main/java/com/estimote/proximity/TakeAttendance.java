package com.estimote.proximity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
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
import com.estimote.proximity.tensorFlowLite.BeaconLocalizer;
//import com.estimote.proximity.tensorFlowLite.BeaconCNN;
//import com.estimote.proximity.tensorFlowLite.BeaconLocalizer;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class TakeAttendance extends AppCompatActivity implements PredictionListener {
    private BeaconManager beaconManager;
    private BeaconRegion regionBlueberry, regionMint, regionCoconut, regionIce;
    private Trilateration trilateration;
    double[] position = new double[2];

    PredictionListener predictionListener = null;

    String tag = "login";

    EditText tvSResultSFX = null;
    EditText tvSResultSFY = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        predictionListener = this;

        //setting beacon
        beaconManager = new BeaconManager(this);
        beaconManager.setForegroundScanPeriod(110L, 0L);
        beaconManager.setBackgroundScanPeriod(110L, 0L);

        EditText nameInput = findViewById(R.id.name_input);
        Button calculateButton = findViewById(R.id.calculate_button);
        TextView tvNameMsg = findViewById(R.id.tvNameMsg);
        TextView tvSResultX = findViewById(R.id.tvSResultX);
        TextView tvSResultY = findViewById(R.id.tvSResultY);
        TextView tvSResult4BX = findViewById(R.id.tvSResult4BX);
        TextView tvSResult4BY = findViewById(R.id.tvSResult4BY);
        TextView tvSResultCNNX = findViewById(R.id.tvSResultCNNX);
        TextView tvSResultCNNY = findViewById(R.id.tvSResultCNNY);
        TextView tvTimeMsg = findViewById(R.id.tvTimeMsg);
        Button btMap = findViewById(R.id.btMap);
        Button btMap4B = findViewById(R.id.btMap4B);
        Button btMapCNN = findViewById(R.id.btMapCNN);
        tvSResultSFX = findViewById(R.id.tvSResultSFX);
        tvSResultSFY = findViewById(R.id.tvSResultSFY);

        double[] x = new double[4];
        x[0] = 520;
        x[1] = 0;
        x[2] = 770;
        x[3] = 265;
        double[] y = new double[4];
        y[0] = 0;
        y[1] = 370;
        y[2] = 550;
        y[3] = 750;
        double[] rssi = new double[4];
        double[] recordedRssi = new double[4];
        int[] recordTime = {0, 0, 0,0};
        int limitedRecordTime = 5;
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
                        int majorIce = 20001;
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


                    protected void onPostExecute() {
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
                                    if (recordTime[0] >= limitedRecordTime) {
                                        states[0] = true;
                                    } else {
                                        recordedRssi[0] = recordedRssi[0] + beacon.getRssi();
                                        recordedTxpower[0] = recordedTxpower[0] + beacon.getMeasuredPower();
                                        Log.e("RSSI", "RSSI 15322 value: " + beacon.getRssi());
                                        recordTime[0]++;
                                    }
                                    break;
                                case 2:
                                    //mint
                                    if (recordTime[1] >= limitedRecordTime) {
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
                                    if (recordTime[2] >= limitedRecordTime) {
                                        states[2] = true;
                                    } else {
                                        recordedRssi[2] = recordedRssi[2] + beacon.getRssi();
                                        recordedTxpower[2] = recordedTxpower[2] + beacon.getMeasuredPower();
                                        Log.e("RSSI", "RSSI 3 value: " + beacon.getRssi());
                                        recordTime[2]++;
                                    }
                                    break;

                                    case 1:
                                    //ice
                                    if (recordTime[3] >= limitedRecordTime) {
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

                        Log.e("states", "states result: blueberry:" + states[0] + ", mint:" + states[1] + ", coconut:" + states[2] + ", ice:" + states[3]);

                        // Calculate the trilateration and store the result in the result array
                        if (states[0] != false && states[1] != false && states[2] != false && states[3] != false) {
                            for (int j = 0; j < states.length; j++) {
                                rssi[j] = recordedRssi[j] / recordTime[j];
                                txPower[j] = recordedTxpower[j] / recordTime[j];
                                Log.e("Location", "Average RSSI value of beacon" + (j+1) + " : " + rssi[j]);
                                Log.e("Location", "Average txPower value of beacon" + (j+1) + " : " + txPower[j]);
                                //reset data set
                                states[j] = false;
                                recordTime[j] = 0;
                                recordedRssi[j] = 0;
                                recordedTxpower[j] = 0;
                            }

                            //cal 3 beacon
                            trilateration = new Trilateration();
                            position = trilateration.calculation(x, y, rssi, txPower);
                            Log.e("Location", "Location result(3 beacon): " + position[0] + ", " + position[1]);
                            tvSResultX.setText(Double.toString(position[0]));
                            tvSResultY.setText(Double.toString(position[1]));

                            //cal 4 beacon
                            position = trilateration.multiCalculation(x, y, rssi, txPower);
                            Log.e("Location", "Location result(4 beacon): " + position[0] + ", " + position[1]);
                            tvSResult4BX.setText(Double.toString(position[0]));
                            tvSResult4BY.setText(Double.toString(position[1]));

                            //cal CNN
                            BeaconLocalizer beaconLocalizer = null;
                            try {
                                beaconLocalizer = new BeaconLocalizer(TakeAttendance.this);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            position = beaconLocalizer.calculatePosition(rssi);
                            Log.e("Location", "Location result(CNN with 3 beacon RSSI): " + position[0] + ", " + position[1]);
                            tvSResultCNNX.setText(Double.toString(position[0]));
                            tvSResultCNNY.setText(Double.toString(position[1]));

                            Log.e("Testing logout bt", "ClickLogout");
                            RF rf = new RF(predictionListener, rssi);
                            rf.execute();

                            Log.e("Location", "Location result(CNN with 3 beacon RSSI): " + position[0] + ", " + position[1]);


                            // Update UI with the result
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    // Update UI with the result
                                    tvNameMsg.setText("Hello, " + nameInput.getText());
                                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                                    String currentDateTime = dateFormat.format(new Date());
                                    tvTimeMsg.setText("Recored time: " + currentDateTime);
                                    long elapsedTime = System.currentTimeMillis() - startTime;
                                    elapsedTime = elapsedTime / 1000;
                                    Toast.makeText(TakeAttendance.this, "Calculations completed in " + elapsedTime + " s", Toast.LENGTH_LONG).show();
                                    // Hide the loading indicator
                                    progressDialog.dismiss();
                                    beaconManager.disconnect();
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
                position[0] = Double.parseDouble(tvSResultX.getText().toString());
                position[1] = Double.parseDouble(tvSResultY.getText().toString());
                intent.putExtra("result", position);
                intent.putExtra("x", x);
                intent.putExtra("y", y);
                intent.putExtra("test", 1);
                startActivityForResult(intent, 1);
            }
        });

        btMap4B.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Map.class);
                position[0] = Double.parseDouble(tvSResult4BX.getText().toString());
                position[1] = Double.parseDouble(tvSResult4BY.getText().toString());
                intent.putExtra("result", position);
                intent.putExtra("x", x);
                intent.putExtra("y", y);
                intent.putExtra("test", 2);
                startActivityForResult(intent, 2);
            }
        });

        btMapCNN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Map.class);
                position[0] = Double.parseDouble(tvSResultCNNX.getText().toString());
                position[1] = Double.parseDouble(tvSResultCNNY.getText().toString());
                intent.putExtra("result", position);
                intent.putExtra("x", x);
                intent.putExtra("y", y);
                intent.putExtra("test", 1);
                startActivityForResult(intent, 1);
            }
        });
    }

    @Override
    public void onPredictionReceived(double[] position) {
        Log.e("RF", "rf123: " + position[0] + " " + position[1]);


        tvSResultSFX.setText("" + position[0]);
        tvSResultSFY.setText("" + position[1]);

    }
}
