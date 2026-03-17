package com.example.weatherapp.controller;

import android.location.Location;

public interface ILocationService {
    interface LocationCallback {
        void onSuccess(Location location);
        void onError(Throwable t);
    }
    void getCurrentLocation(LocationCallback callback);
    boolean isLocationAvailable();
    void stopLocationUpdates();
    int getLocationPermissionRequestCode();
    void checkDeviceLocationSettings(android.app.Activity activity);
    void forceActiveLocationUpdate(LocationCallback callback);
    int REQUEST_CHECK_SETTINGS = 1002;
} 