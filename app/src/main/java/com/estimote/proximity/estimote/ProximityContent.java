package com.estimote.proximity.estimote;

//
// Running into any issues? Drop us an email to: contact@estimote.com
//

public class ProximityContent {

    private String title;
    private String subtitle;
    private String signal;

    ProximityContent(String title, String subtitle) {
        this.title = title;
        this.subtitle = subtitle;
    }

    ProximityContent(String title, String subtitle, String signal) {
        this.title = title;
        this.subtitle = subtitle;
        this.signal = signal;
    }

    String getTitle() {
        return title;
    }

    String getSubtitle() {
        return subtitle;
    }
}
