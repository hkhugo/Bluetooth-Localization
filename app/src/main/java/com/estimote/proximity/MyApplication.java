package com.estimote.proximity;

import android.app.Application;

import com.estimote.coresdk.service.BeaconManager;
import com.estimote.proximity_sdk.api.EstimoteCloudCredentials;

//
// Running into any issues? Drop us an email to: contact@estimote.com
//

public class MyApplication extends Application {

    private BeaconManager beaconManager;

    @Override
    public void onCreate() {
        super.onCreate();

        beaconManager = new BeaconManager(getApplicationContext());
    }

    public EstimoteCloudCredentials cloudCredentials =
            new EstimoteCloudCredentials("hugo6d-gmail-com-s-proximi-4ef", "e0b131cd8079e74f020704cd1dcad0c4");
}
