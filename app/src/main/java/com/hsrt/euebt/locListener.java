package com.hsrt.euebt;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

/**
 * Created by Moe on 24.01.2017.
 */

public class locListener implements LocationListener {
    @Override
    public void onLocationChanged(Location location) {
        if(location != null)
        {
            System.out.println("LAT ---> " + location.getLatitude());
            System.out.println("LONG ---> " + location.getLongitude());
        }

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
