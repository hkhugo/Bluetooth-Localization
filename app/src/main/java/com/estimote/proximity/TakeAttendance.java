package com.estimote.proximity;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.estimote.proximity.estimote.ProximityContentAdapter;
import com.estimote.proximity.estimote.ProximityContentManager;

public class TakeAttendance extends AppCompatActivity {

    private ProximityContentManager proximityContentManager;
    private ProximityContentAdapter proximityContentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        //setting beacon
        proximityContentAdapter = new ProximityContentAdapter(this);

        EditText nameInput = findViewById(R.id.name_input);
        Button calculateButton = findViewById(R.id.calculate_button);
        TextView tvSResultX = findViewById(R.id.tvSResultX);
        TextView tvSResultY = findViewById(R.id.tvSResultY);

        calculateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout login_layout = findViewById(R.id.login_layout);
                login_layout.setVisibility(View.VISIBLE);


                String name = nameInput.getText().toString();
                // Show loading indicator
                ProgressDialog progressDialog = ProgressDialog.show(TakeAttendance.this, "Calculating", "Please wait...", true);

                // Run calculation on a background thread
                new AsyncTask<Void, Void, double[]>() {
                    @Override
                    protected double[] doInBackground(Void... params) {
                        // Use the calculation method you provided to calculate x and y positions
//                        double[] position = calculation(x, y, rssi, txpower);
                        double[] position = {15, 15};
                        return position;
                    }

                    @Override
                    protected void onPostExecute(double[] position) {
                        // Dismiss loading indicator
                        progressDialog.dismiss();

                        // Update UI with the result
                        tvSResultX.setText(Double.toString(position[0]));
                        tvSResultY.setText(Double.toString(position[1]));
                    }
                }.execute();
            }
        });
    }

    private void startProximityContentManager() {
        proximityContentManager = new ProximityContentManager(this, proximityContentAdapter, ((MyApplication) getApplication()).cloudCredentials);
        proximityContentManager.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (proximityContentManager != null)
            proximityContentManager.stop();
    }
}
