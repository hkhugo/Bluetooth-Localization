package com.estimote.proximity.estimote;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.estimote.coresdk.observation.region.beacon.BeaconRegion;
import com.estimote.coresdk.recognition.packets.Beacon;
import com.estimote.coresdk.service.BeaconManager;
import com.estimote.proximity.R;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

//
// Running into any issues? Drop us an email to: contact@estimote.com
//

public class ProximityContentAdapter extends BaseAdapter {

    private BeaconManager beaconManager;
    private BeaconRegion region;

    private Context context;

    public ProximityContentAdapter(Context context) {
        this.context = context;
    }

    private List<ProximityContent> nearbyContent = new ArrayList<>();

    public void setNearbyContent(List<ProximityContent> nearbyContent) {
        this.nearbyContent = nearbyContent;
    }

    @Override
    public int getCount() {
        return nearbyContent.size();
    }

    @Override
    public Object getItem(int position) {
        return nearbyContent.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater =
                    (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert inflater != null;

            convertView = inflater.inflate(R.layout.content_view, parent, false);
        }

        ProximityContent content = nearbyContent.get(position);

        TextView title = convertView.findViewById(R.id.title);
        TextView subtitle = convertView.findViewById(R.id.subtitle);
        TextView major = convertView.findViewById(R.id.major);
        TextView minor = convertView.findViewById(R.id.minor);
        TextView rssi = convertView.findViewById(R.id.rssi);

        title.setText("Title: " + content.getTitle());
        subtitle.setText("subtitle: " + content.getSubtitle());
        major.setText("major: " + String.valueOf(content.getMajor()));
        minor.setText("minor: " + String.valueOf(content.getMinor()));

        beaconManager = new BeaconManager(context);
        // Define a region to range for
        String regionName = "Estimote iBeacon";
        UUID regionUUID = UUID.fromString("B9407F30-F5F8-466E-AFF9-25556B57FE6D");
        region = new BeaconRegion(regionName, regionUUID, content.getMajor(), content.getMinor());
        // Start ranging for the specified region
        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                beaconManager.startRanging(region);
            }
        });

        beaconManager.setRangingListener(new BeaconManager.BeaconRangingListener() {
            @Override
            public void onBeaconsDiscovered(BeaconRegion region, List<Beacon> beacons) {

                // The result is a list of Beacon objects.
                for (Beacon beacon : beacons) {
                    // You get the UUID and the RSSI with:
                    Log.e("RSSI", "RSSI value: " + beacon.getRssi());
                    Log.e("power", "power value: " + beacon.getMeasuredPower());
                    rssi.setText("RSSI: " + String.valueOf(beacon.getRssi()));
                    // Now, call your own methods to work with the values.
                }
            }
        });

        convertView.setBackgroundColor(Utils.getEstimoteColor(content.getTitle()));

        return convertView;
    }
}
