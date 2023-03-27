package com.estimote.proximity;

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
        }

        double[] p = { 0.0, 0.0 };

        double a11 = 2 * (x[0] - x[2]);

        double a12 = 2 * (y[0] - y[2]);

        double b1 = Math.pow(x[0], 2) - Math.pow(x[2], 2) + Math.pow(y[0], 2) - Math.pow(y[2], 2) + Math.pow(rssi[2], 2)
                - Math.pow(rssi[0], 2);

        double a21 = 2 * (x[1] - x[2]);

        double a22 = 2 * (y[1] - y[2]);

        double b2 = Math.pow(x[1], 2) - Math.pow(x[2], 2) + Math.pow(y[1], 2) - Math.pow(y[2], 2) + Math.pow(rssi[2], 2)
                - Math.pow(rssi[1], 2);

        p[0] = (b1 * a22 - a12 * b2) / (a11 * a22 - a12 * a21);

        p[1] = (a11 * b2 - b1 * a21) / (a11 * a22 - a12 * a21);

        return p;
    }
}
