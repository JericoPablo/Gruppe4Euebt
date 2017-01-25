package com.hsrt.euebt;

import android.location.Location;

/**
 * Created by Johannes on 24.01.2017.
 */

/**
 * A container class which allows the storage of an address along with the according GPS location.
 */
public class TrainingPlace {

    private String address;
    private Location location;

    public TrainingPlace(String address, Location location) {
        this.address = address;
        this.location = location;
    }

    public String getAddress() {
        return this.address;
    }

    public Location getLocation() {
        return this.location;
    }
}