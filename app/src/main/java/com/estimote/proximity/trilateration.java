package com.estimote.proximity;

public class trilateration {

    public static double[] calculation(double[] x, double[] y, double[] rssi) {


        double[] d = { 0.0, 0.0 };

        double a11 = 2 * (x[1] - x[3]);

        double a12 = 2 * (y[1] - y[3]);

        double b1 = Math.pow(x[1], 2) - Math.pow(x[3], 2) + Math.pow(y[1], 2) - Math.pow(y[3], 2) + Math.pow(rssi[3], 2)
                - Math.pow(rssi[1], 2);

        double a21 = 2 * (x[2] - x[3]);

        double a22 = 2 * (y[2] - y[3]);

        double b2 = Math.pow(x[2], 2) - Math.pow(x[3], 2) + Math.pow(y[2], 2) - Math.pow(y[3], 2) + Math.pow(rssi[3], 2)
                - Math.pow(rssi[2], 2);

        d[0] = (b1 * a22 - a12 * b2) / (a11 * a22 - a12 * a21);

        d[1] = (a11 * b2 - b1 * a21) / (a11 * a22 - a12 * a21);

        return d;
    }
}
