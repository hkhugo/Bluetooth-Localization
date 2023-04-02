package com.estimote.proximity.tensorFlowLite;

//import org.tensorflow.lite.Interpreter;

import android.content.Context;
import android.util.Log;

import com.estimote.proximity.algorithms.Trilateration;
import com.estimote.proximity.ml.BeaconLocalizationModel;
import com.estimote.proximity.ml.BeaconLocalizationModel3gateways;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class BeaconLocalizer3Beacon {

    public void comparison(Context context) {
        double[] x = new double[3];
        x[0] = 4.80;
        x[1] = 8.34;
        x[2] = 0.45;
        double[] y = new double[3];
        y[0] = 0;
        y[1] = 5.32;
        y[2] = 5.54;
        double[] rssi = {-85, -78, -81};
        double[] txpower = {-71, -71, -81};

        // Calculate the position using trilateration
        double[] position1 = Trilateration.calculation(x, y, rssi, txpower);
        Log.i("Ai", "Position using trilateration: (" + position1[0] + ", " + position1[1] + ")");

        // Calculate the position using CNN
        double[] position2 = calculatePosition(x, y, rssi, txpower, context);
        Log.i("Ai", "Position using CNN: (" + position2[0] + ", " + position2[1] + ")");
    }

    public static double[] calculatePosition(double[] x, double[] y, double[] rssi, double[] txpower, Context context) {
        double[] position = new double[2];

        try {
            BeaconLocalizationModel3gateways model = BeaconLocalizationModel3gateways.newInstance(context);

            // Create inputs for reference.
            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 3}, DataType.FLOAT32);
            ByteBuffer byteBuffer = ByteBuffer.allocate(4 * 3);
            for (int i = 0; i < 3; i++) {
                byteBuffer.putFloat((float) rssi[i]);
            }
            inputFeature0.loadBuffer(byteBuffer);

            // Run model inference and get result.
            BeaconLocalizationModel3gateways.Outputs outputs = model.process(inputFeature0);
            TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

            // Release model resources if no longer used.
            model.close();

            // Convert output to position array
            position[0] = outputFeature0.getFloatArray()[0];
            position[1] = outputFeature0.getFloatArray()[1];

        } catch (IOException e) {
            // TODO Handle the exception
        }

        return position;
    }
}















