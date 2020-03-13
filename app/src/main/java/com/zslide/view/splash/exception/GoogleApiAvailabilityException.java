package com.zslide.view.splash.exception;

import com.google.android.gms.common.GoogleApiAvailability;

/**
 * Created by chulwoo on 2018. 1. 2..
 */

public class GoogleApiAvailabilityException extends RuntimeException {

    private final GoogleApiAvailability googleApiAvailability;

    public GoogleApiAvailabilityException(GoogleApiAvailability googleApiAvailability) {
        this.googleApiAvailability = googleApiAvailability;
    }

    public GoogleApiAvailability getApiAvailability() {
        return googleApiAvailability;
    }
}
