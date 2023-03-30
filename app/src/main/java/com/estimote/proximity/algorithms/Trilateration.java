package com.estimote.proximity.algorithms;

import android.util.Log;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.DecompositionSolver;
import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.linear.SingularMatrixException;
import org.apache.commons.math3.linear.SingularValueDecomposition;

public class Trilateration {
    public static double getDistance(Double rssi, double txPower) {
        /*
         * RSSI = TxPower - 10 * n * lg(d)
         * n = 2 (in free space)
         * d = 10 ^ ((TxPower - RSSI) / (10 * n))
         */
        int n = 2;

        return Math.pow(10d, ((double) txPower - rssi) / (10 * n));
    }

    public static double[] calculation(double[] x, double[] y, double[] rssi, double[] txpower) {

        double distance[] = new double[3];

        for (int i = 0; i < 3; i++){
            distance[i] = getDistance(rssi[i], txpower[i]);
            Log.i("distance ", (String.valueOf(i) + " " + String.valueOf(distance[i] )));
        }

        double[] p = { 0.0, 0.0 };

        double a11 = 2 * (x[0] - x[2]);

        double a12 = 2 * (y[0] - y[2]);

        double b1 = Math.pow(x[0], 2) - Math.pow(x[2], 2) + Math.pow(y[0], 2) - Math.pow(y[2], 2) + Math.pow(distance[2], 2)
                - Math.pow(distance[0], 2);

        double a21 = 2 * (x[1] - x[2]);

        double a22 = 2 * (y[1] - y[2]);

        double b2 = Math.pow(x[1], 2) - Math.pow(x[2], 2) + Math.pow(y[1], 2) - Math.pow(y[2], 2) + Math.pow(distance[2], 2)
                - Math.pow(distance[1], 2);

        p[0] = (b1 * a22 - a12 * b2) / (a11 * a22 - a12 * a21);

        p[1] = (a11 * b2 - b1 * a21) / (a11 * a22 - a12 * a21);

        return p;
    }

    //four beacon in used
    public static double[] multiCalculation(double[] x, double[] y, double[] rssi, double[] txpower) {

        double distance[] = new double[4];

        for (int i = 0; i < 4; i++){
            distance[i] = getDistance(rssi[i], txpower[i]);
            Log.i("distance ", (String.valueOf(i) + " " + String.valueOf(distance[i] )));
        }

        double[] p = { 0.0, 0.0 };

        double a11 = 2 * (x[0] - x[3]);
        double a12 = 2 * (y[0] - y[3]);
        double b1 = Math.pow(x[0], 2) - Math.pow(x[3], 2) + Math.pow(y[0], 2) - Math.pow(y[3], 2) + Math.pow(distance[3], 2) - Math.pow(distance[0], 2);

        double a21 = 2 * (x[1] - x[3]);
        double a22 = 2 * (y[1] - y[3]);
        double b2 = Math.pow(x[1], 2) - Math.pow(x[3], 2) + Math.pow(y[1], 2) - Math.pow(y[3], 2) + Math.pow(distance[3], 2) - Math.pow(distance[1], 2);

        double a31 = 2 * (x[2] - x[3]);
        double a32 = 2 * (y[2] - y[3]);
        double b3 = Math.pow(x[2], 2) - Math.pow(x[3], 2) + Math.pow(y[2], 2) - Math.pow(y[3], 2) + Math.pow(distance[3], 2) - Math.pow(distance[2], 2);

        double[][] A = {
                {a11, a12},
                {a21, a22},
                {a31, a32}
        };

        double[] B = {b1, b2, b3};

        RealMatrix a = new Array2DRowRealMatrix(A, false);
        DecompositionSolver solver = new SingularValueDecomposition(a).getSolver();
        RealVector b = new ArrayRealVector(B, false);
        RealVector c = solver.solve(b);

        p[0] = c.getEntry(0);
        p[1] = c.getEntry(1);

        return p;
    }
}
