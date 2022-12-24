package com.estimote.proximity;

import android.os.Bundle;
import android.util.Log;
import android.widget.GridView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.estimote.mustard.rx_goodness.rx_requirements_wizard.Requirement;
import com.estimote.mustard.rx_goodness.rx_requirements_wizard.RequirementsWizardFactory;
import com.estimote.proximity.estimote.ProximityContentAdapter;
import com.estimote.proximity.estimote.ProximityContentManager;

import java.util.List;

import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;


//
// Running into any issues? Drop us an email to: contact@estimote.com
//

public class ListDevice extends AppCompatActivity {

    private ProximityContentManager proximityContentManager;
    private ProximityContentAdapter proximityContentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_device);

        proximityContentAdapter = new ProximityContentAdapter(this);
        GridView gridView = findViewById(R.id.gridView);
        gridView.setAdapter(proximityContentAdapter);


        RequirementsWizardFactory
                .createEstimoteRequirementsWizard()
                .fulfillRequirements(this,
                        new Function0<Unit>() {
                            @Override
                            public Unit invoke() {
                                Log.d("app", "requirements fulfilled");
                                startProximityContentManager();
                                return null;
                            }
                        },
                        new Function1<List<? extends Requirement>, Unit>() {
                            @Override
                            public Unit invoke(List<? extends Requirement> requirements) {
                                Log.e("app", "requirements missing: " + requirements);
                                return null;
                            }
                        },
                        new Function1<Throwable, Unit>() {
                            @Override
                            public Unit invoke(Throwable throwable) {
                                Log.e("app", "requirements error: " + throwable);
                                return null;
                            }
                        });

//        BeaconManager beaconManager = new BeaconManager(this);
//        beaconManager.setRangingListener(new BeaconManager.BeaconRangingListener() {
//            @Override
//            public void onBeaconsDiscovered(BeaconRegion region, List<Beacon> beacons) {
//                // The result is a list of Beacon objects.
//                for (Beacon beacon : beacons) {
//                    // You get the UUID and the RSSI with:
//                    beacon.getProximityUUID();
//                    beacon.getRssi();
//                    // Now, call your own methods to work with the values.
//                }
//            }
//        });
    }

    private void startProximityContentManager() {
        proximityContentManager = new ProximityContentManager(this, proximityContentAdapter, ((MyApplication) getApplication()).cloudCredentials);
        proximityContentManager.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (proximityContentManager != null)
            proximityContentManager.stop();
    }

}
