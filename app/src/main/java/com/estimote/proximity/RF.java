package com.estimote.proximity;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;



public class RF extends AsyncTask<String, Double, String> {

    String TAG = "RF";
    private PredictionListener predictionListener; // interface reference
    double rssi[];

    public RF(PredictionListener listener, double[] rssi) {
        this.predictionListener = listener;
        this.rssi = rssi;
    }

    @Override
    protected String doInBackground(String... params) {

        // Create an OkHttpClient instance
        OkHttpClient client = new OkHttpClient();

        // Define the input data as a JSON string
        String json = "{\"input_data\": [ " + rssi[0] + " ," + rssi[1] + " ,"+ rssi[2] + " ,"+ rssi[3]+"]}";

        // Create a RequestBody with the input data
        RequestBody body = RequestBody.create(json, MediaType.parse("application/json; charset=utf-8"));

        // Create a Request object
        Request request = new Request.Builder()
                .url("http://hkhugo.asuscomm.com:10079/predict")
                .post(body)
                .build();

        // Send the request and get the response
        try (Response response = client.newCall(request).execute()) {
            // Get the response body as a string
            String responseBody = response.body().string();
            Log.e("RF", "rf123: " + responseBody);
            return responseBody;
            // TODO: Parse the JSON response and use the prediction
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(String result) {
        // TODO: Parse the JSON response and use the prediction
        try {
            Log.e("onPostExecute", "onPostExecute: ");
            // Parse the JSON response
            JSONObject jsonObject = new JSONObject(result);

            // Extract the prediction from the JSON object
            JSONArray predictionArray = jsonObject.getJSONArray("prediction").getJSONArray(0);
            double[] prediction = new double[predictionArray.length()];
            prediction[0] = predictionArray.getDouble(0);
            prediction[1] = predictionArray.getDouble(1);
            Log.e("prediction", "prediction: " + prediction[0] + " " + prediction[1]);
            // Call the interface method with the 2nd position of the prediction array
            predictionListener.onPredictionReceived(prediction);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}