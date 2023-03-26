package com.estimote.proximity.tensorFlowLite;

//import org.tensorflow.lite.Interpreter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.os.Environment;

    public class BeaconLocalizer {
//        private Interpreter tflite;

//        public BeaconLocalizer(Context context) throws Exception {
//            // Load the TensorFlow Lite model from the assets folder
//            MappedByteBuffer modelBuffer = loadModel(context);
//            tflite = new Interpreter(modelBuffer);
//        }
//
//        public float[] localizeBeacon(float[] rssi) {
//            float[][] input = {rssi};
//            float[] output = new float[1]; // Update the output array shape to [1]
//            tflite.run(input, output);
//            return output;
//        }

        public static MappedByteBuffer loadModelFile(Activity activity) throws IOException {
            AssetFileDescriptor fileDescriptor = activity.getAssets().openFd("my_model.tflite");
            FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
            FileChannel fileChannel = inputStream.getChannel();
            long startOffset = fileDescriptor.getStartOffset();
            long declaredLength = fileDescriptor.getDeclaredLength();
            return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
        }
    }







