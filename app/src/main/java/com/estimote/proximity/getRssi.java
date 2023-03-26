package com.estimote.proximity;

import android.content.Context;
import android.util.Log;

import com.estimote.coresdk.observation.region.beacon.BeaconRegion;
import com.estimote.coresdk.recognition.packets.Beacon;
import com.estimote.coresdk.service.BeaconManager;

import java.util.List;
import java.util.UUID;

public class getRssi {

    private BeaconManager beaconManager;
    private BeaconRegion region;

    private Context context;
    private int rssi;

    public int getRssi(String title) {
        beaconManager = new BeaconManager(context);
        // Define a region to range for
        String regionName = "Estimote iBeacon";
        UUID regionUUID = UUID.fromString("B9407F30-F5F8-466E-AFF9-25556B57FE6D");
        int major = 0;
        int minor = 0;
        switch (title) {
            case "blueberry":
                major = 10016;
                minor = 15322;
                break;
            case "ice":
                major = 20001;
                minor = 1;
                break;
            case "coconut":
                major = 30001;
                minor = 3;
                break;
        }

        region = new BeaconRegion(regionName, regionUUID, major, minor);
        // Start ranging for the specified region
        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                beaconManager.startRanging(region);
            }
        });

        beaconManager.setRangingListener(new BeaconManager.BeaconRangingListener() {
            @Override
            public void onBeaconsDiscovered(BeaconRegion beaconRegion, List<Beacon> beacons) {
                // The result is a list of Beacon objects.
                for (Beacon beacon : beacons) {
                    // You get the UUID and the RSSI with:
                    Log.e("RSSI", "RSSI value: " + beacon.getRssi());
                    Log.e("power", "power value: " + beacon.getMeasuredPower());
                    rssi = beacon.getRssi();
                    // Now, call your own methods to work with the values.
                }
            }
        });
        return rssi;
    }

}
