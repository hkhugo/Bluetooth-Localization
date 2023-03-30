package com.estimote.proximity;

import com.estimote.proximity.algorithms.Trilateration;

public class LocationComparison {


    public void comparison() throws Exception {
        double[] x = { 1.0, 2.0, 3.0 };
        double[] y = { 4.0, 5.0, 6.0 };
        double[] rssi = { -50.0, -60.0, -70.0, -80.0 };
        double txpower[] = new double[3];
        txpower[0] = -59;
        txpower[1] = -59;
        txpower[2] = -59;

        // Calculate the position using trilateration
        double[] position1 = Trilateration.calculation(x, y, rssi, txpower);

        // Calculate the position using BeaconLocalizer
        //...


        System.out.println("Position using trilateration: (" + position1[0] + ", " + position1[1] + ")");
//        System.out.println("Position using BeaconLocalizer: (" + position2[0] + ", " + position2[1] + ")");

    }
}
