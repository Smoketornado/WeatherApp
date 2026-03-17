package com.example.weatherapp.controller;

import android.app.Activity;
import android.location.Location;

public class LocationServiceImpl implements ILocationService {
    private final LocationController locationController;
    private final Activity activity;
    private Location lastKnownLocation;

    public LocationServiceImpl(Activity activity) {
        this.locationController = new LocationController(activity);
        this.activity = activity;
    }

    @Override
    public void getCurrentLocation(LocationCallback callback) {
        locationController.setAppCallback(new LocationController.AppLocationCallback() {
            @Override
            public void onLocationReceived(Location location) {
                lastKnownLocation = location;
                callback.onSuccess(location);
            }
            @Override
            public void onLocationError(String error) {
                callback.onError(new Exception(error));
            }
            @Override
            public void onPermissionDenied() {
                callback.onError(new SecurityException("Location permission denied"));
            }
            @Override
            public void onLocationSettingsNeeded() {
                callback.onError(new Exception("Location settings needed"));
            }
        });
        // Starte den Standortprozess!
        locationController.initiateLocationRetrieval(activity);
    }

    @Override
    public boolean isLocationAvailable() {
        return lastKnownLocation != null;
    }

    // Methode, um den Standort zu aktualisieren (z.B. von außen aufrufbar)
    public void updateLocation(Location location) {
        this.lastKnownLocation = location;
    }

    @Override
    public void stopLocationUpdates() {
        locationController.stopLocationUpdates();
    }

    @Override
    public int getLocationPermissionRequestCode() {
        return locationController.getLocationPermissionRequestCode();
    }

    @Override
    public void checkDeviceLocationSettings(android.app.Activity activity) {
        locationController.checkDeviceLocationSettings(activity);
    }

    // Neue Methode: Erzwingt ein aktives Location-Update
    public void forceActiveLocationUpdate(LocationCallback callback) {
        locationController.setAppCallback(new LocationController.AppLocationCallback() {
            @Override
            public void onLocationReceived(Location location) {
                lastKnownLocation = location;
                callback.onSuccess(location);
            }
            @Override
            public void onLocationError(String error) {
                callback.onError(new Exception(error));
            }
            @Override
            public void onPermissionDenied() {
                callback.onError(new SecurityException("Location permission denied"));
            }
            @Override
            public void onLocationSettingsNeeded() {
                callback.onError(new Exception("Location settings needed"));
            }
        });
        locationController.requestActiveLocationUpdates(activity);
    }
} 