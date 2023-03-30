package com.estimote.proximity.tensorFlowLite;

//import org.tensorflow.lite.Interpreter;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import android.content.Context;
import android.util.Log;

import com.estimote.proximity.algorithms.Trilateration;
import com.estimote.proximity.ml.BeaconLocalizationModel;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

public class BeaconLocalizer {

    private BeaconLocalizationModel model;

    public BeaconLocalizer(Context context) throws IOException {
        model = BeaconLocalizationModel.newInstance(context);
    }

    public void close() {
        model.close();
    }

    public double[] calculatePosition(double[] rssi) {
        double[] position = null;

        // Create input buffer and copy rssi values into it
        ByteBuffer byteBuffer = ByteBuffer.allocate(84);  // 21 values * 4 bytes per value
        byteBuffer.order(ByteOrder.nativeOrder());
        FloatBuffer floatBuffer = byteBuffer.asFloatBuffer();
        for (double value : rssi) {
            floatBuffer.put((float) value);
        }
        // Fill remaining values with 0.0
        for (int i = rssi.length; i < 21; i++) {
            floatBuffer.put(0.0f);
        }
        TensorBuffer inputBuffer = TensorBuffer.createFixedSize(new int[]{1, 21}, DataType.FLOAT32);
        inputBuffer.loadBuffer(byteBuffer);

        // Run model inference
        BeaconLocalizationModel.Outputs outputs = model.process(inputBuffer);
        TensorBuffer outputBuffer = outputs.getOutputFeature0AsTensorBuffer();

        // Get output values and convert to array
        float[] outputValues = outputBuffer.getFloatArray();
        position = new double[]{outputValues[0], outputValues[1]};

        return position;
    }


    public void comparison(Context context){
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


        // Calculate the position using BeaconLocalizer
        BeaconLocalizer localizer = null;
        try {
            localizer = new BeaconLocalizer(context);
            double[] position2 = localizer.calculatePosition(rssi);
            Log.i("Ai", "Position using BeaconLocalizer: (" + position2[0] + ", " + position2[1] + ")");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (localizer != null) {
                localizer.close();
            }
        }
    }
}







