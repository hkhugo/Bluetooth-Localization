package com.estimote.proximity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.estimote.coresdk.common.requirements.SystemRequirementsChecker;
import com.estimote.coresdk.observation.region.beacon.BeaconRegion;
import com.estimote.coresdk.recognition.packets.Beacon;
import com.estimote.coresdk.service.BeaconManager;
import com.estimote.proximity.tensorFlowLite.BeaconLocalizer;

//import org.tensorflow.lite.DataType;
//import org.tensorflow.lite.Interpreter;
//import org.tensorflow.lite.Tensor;
//import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;


import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    DrawerLayout drawerLayout;
    TextView navigationTV;

    private BeaconManager beaconManager;
    private BeaconRegion region;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);

        navigationTV = findViewById(R.id.navigationTV);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationTV.setText("Trilateration Algorithm calculator");

        EditText ptRssi1 = findViewById(R.id.ptRSSI1);
        EditText ptRSSI2 = findViewById(R.id.ptRSSI2);
        EditText ptRSSI3 = findViewById(R.id.ptRSSI3);

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

    public void ClickLogout(View view) throws Exception {
//        logout(this);

        // for testing part
//        double[] x = {1.0, 2.0, 3.0};
//        double[] y = {4.0, 5.0, 6.0};
//        double[] rssi = {-50.0, -60.0, -70.0};
//        double txpower = -60.0;
//
//// Calculate the position using trilateration
//        double[] position1 = trilateration.calculation(x, y, rssi, txpower);
//
//// Calculate the position using BeaconLocalizer
////        BeaconLocalizer beaconLocalizer = new BeaconLocalizer(this);
////        float[] rssiFloat = new float[rssi.length];
////        for (int i = 0; i < rssi.length; i++) {
////            rssiFloat[i] = (float) rssi[i];
////        }
////        float[] position2 = beaconLocalizer.localizeBeacon(rssiFloat);
////
//
//// Convert the input data to the appropriate format for use with the Interpreter
//        float[][] input = new float[1][rssi.length];
//        for (int i = 0; i < rssi.length; i++) {
//            input[0][i] = (float) rssi[i];
//        }
//
//        // Run inference on the input data
//        Interpreter tflite = new Interpreter(BeaconLocalizer.loadModelFile(this));
//
//        // Get the shape of the output tensor
//        int[] outputShape = tflite.getOutputTensor(0).shape();
//        int outputSize = outputShape[1];
//
//        // Create a Java object with the same shape as the output tensor
//        float[][] output = new float[1][outputSize];
//        tflite.run(input, output);
//
//        // Use the output of the model
//        float x_pred = output[0][0];
//        float y_pred = output[0][1];
//
//        Log.i("result", ("Position using trilateration: (" + position1[0] + ", " + position1[1] + ")"));
//        Log.i("result", ("Position using model: (" + x_pred + ", " + y_pred + ")"));
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
        Log.i(TAG, "Clicked calculate");
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
        TextView tvResultX = findViewById(R.id.tvResultX);
        TextView tvResultY = findViewById(R.id.tvResultY);

        double txPower = Double.parseDouble(ptTx.getText().toString());
        double[] result = new double[2];
        double[] x = new double[3];
        x[0] = Double.parseDouble(ptx1.getText().toString());
        x[1] = Double.parseDouble(ptx2.getText().toString());
        x[2] = Double.parseDouble(ptx3.getText().toString());
        double[] y = new double[3];
        y[0] = Double.parseDouble(pty1.getText().toString());
        y[1] = Double.parseDouble(pty2.getText().toString());
        y[2] = Double.parseDouble(pty3.getText().toString());
        double[] rssi = new double[3];
        rssi[0] = Double.parseDouble(ptRssi1.getText().toString());
        rssi[1] = Double.parseDouble(ptRSSI2.getText().toString());
        rssi[2] = Double.parseDouble(ptRSSI3.getText().toString());


        switch (view.getId()) {
            case R.id.btSubmit:
                result = trilateration.calculation(x, y, rssi, txPower);
                tvResultX.setText(Double.toString(result[0]));
                tvResultY.setText(Double.toString(result[1]));
                break;

            case R.id.btShowmap:
                Log.i(TAG, "clicked map now");
                result = trilateration.calculation(x, y, rssi, txPower);
                Intent intent;
                intent = new Intent(getApplicationContext(), Map.class);
                intent.putExtra("result", result);
                intent.putExtra("x", x);
                intent.putExtra("y", y);
                intent.putExtra("test", 1);
                startActivity(intent);
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

}
