package com.zslide.utils;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.text.TextUtils;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import rx.Observable;

/**
 * Created by chulwoo on 16. 3. 22..
 */
public class LocationUtil {

    public static Observable<String> getAddress(Context context, double lat, double lng) {
        return Observable.zip(Observable.just(lat), Observable.just(lng),
                (latitude, longitude) -> {
                    Geocoder geocoder = new Geocoder(context, Locale.KOREA);
                    String addressStr = null;

                    try {
                        List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
                        if (addresses != null && addresses.size() > 0) {
                            Address address = addresses.get(0);
                            addressStr = address.getThoroughfare();
                            if (TextUtils.isEmpty(addressStr)) {
                                addressStr = address.getLocality();
                            }
                            if (TextUtils.isEmpty(addressStr)) {
                                addressStr = address.getAdminArea();
                            }
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    return addressStr;
                });
    }
}
