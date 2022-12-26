package com.estimote.proximity.estimote;

//
// Running into any issues? Drop us an email to: contact@estimote.com
//

public class ProximityContent {

    private String title;
    private String subtitle;
    private String rssi;

//    ProximityContent(String title, String subtitle) {
//        this.title = title;
//        this.subtitle = subtitle;
//    }

    ProximityContent(String title, String subtitle, String rssi) {
        this.title = title;
        this.subtitle = subtitle;
        this.rssi = rssi;
    }

    String getTitle() {
        return title;
    }

    String getSubtitle() {
        return subtitle;
    }

    String getRssi() {
        return rssi;
    }
}
