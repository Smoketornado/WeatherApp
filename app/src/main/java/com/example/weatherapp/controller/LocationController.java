package com.example.weatherapp.controller;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler; // Import Handler
import android.os.Looper;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback; // Google's LocationCallback
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

/**
 * Controller class for location operations
 * Handles location permissions, device settings check, and current location retrieval
 */
public class LocationController {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    // Optional: Request code for returning from location settings
    public static final int REQUEST_CHECK_SETTINGS = 1002;


    private FusedLocationProviderClient fusedLocationClient;
    private AppLocationCallback appCallback; // Renamed to avoid confusion
    private com.google.android.gms.location.LocationCallback googleLocationCallback; // Google's callback

    // Handler and Runnable for location request timeout
    private final Handler timeoutHandler = new Handler(Looper.getMainLooper());
    private Runnable timeoutRunnable;

    // Interface für die App, um Standort-Ergebnisse und Fehler zu empfangen
    public interface AppLocationCallback {
        void onLocationReceived(Location location);
        void onLocationError(String error);
        void onPermissionDenied();
        void onLocationSettingsNeeded(); // Neuer Callback, um die Activity zu informieren
    }

    public LocationController(Context context) {
        this.fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
    }

    public void setAppCallback(AppLocationCallback callback) {
        this.appCallback = callback;
    }

    public boolean hasLocationPermission(Context context) {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    public void requestLocationPermission(Activity activity) {
        ActivityCompat.requestPermissions(
                activity,
                new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION // Coarse ist optional, wenn Fine angefragt wird
                },
                LOCATION_PERMISSION_REQUEST_CODE
        );
    }

    /**
     * Startet den gesamten Prozess des Standortabrufs:
     * 1. Berechtigungen prüfen/anfordern
     * 2. Gerätestandorteinstellungen prüfen
     * 3. Standort abrufen
     */
    public void initiateLocationRetrieval(Activity activity) {
        if (!hasLocationPermission(activity)) {
            requestLocationPermission(activity); // Die Activity muss onRequestPermissionsResult behandeln
            // und dann ggf. erneut initiateLocationRetrieval aufrufen
            // oder direkt checkDeviceLocationSettings
            return;
        }
        // Wenn Berechtigungen vorhanden sind, prüfe die Geräteeinstellungen
        checkDeviceLocationSettings(activity);
    }


    /**
     * Prüft, ob die Standortdienste des Geräts aktiviert sind.
     * Wenn nicht, wird der appCallback.onLocationSettingsNeeded() aufgerufen,
     * damit die Activity den Nutzer zu den Einstellungen leiten kann.
     */
    public void checkDeviceLocationSettings(Activity activity) {
        LocationManager locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        boolean gpsEnabled = false;
        boolean networkEnabled = false;

        try {
            gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ignored) {}
        try {
            networkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ignored) {}

        if (!gpsEnabled && !networkEnabled) {
            if (appCallback != null) {
                appCallback.onLocationSettingsNeeded(); // Activity soll Dialog anzeigen
            }
        } else {
            // Standortdienste sind auf dem Gerät aktiviert, fahre mit dem Abruf fort
            fetchActualLocation(activity);
        }
    }

    /**
     * Ruft den Standort ab (zuerst letzter bekannter, dann aktiv).
     * Diese Methode sollte aufgerufen werden, NACHDEM Berechtigungen erteilt
     * UND Gerätestandorteinstellungen als aktiviert befunden wurden.
     */
    @SuppressWarnings("MissingPermission") // Berechtigungen werden in initiateLocationRetrieval geprüft
    public void fetchActualLocation(Context context) {
        if (!hasLocationPermission(context)) { // Doppelte Prüfung zur Sicherheit
            if (appCallback != null) appCallback.onPermissionDenied();
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(location -> {
                    // Check if the location is not null and is relatively recent (e.g., < 2 minutes old)
                    if (location != null && (System.currentTimeMillis() - location.getTime()) < 120000) {
                        if (appCallback != null) {
                            appCallback.onLocationReceived(location);
                        }
                    } else {
                        // If last location is null or too old, request a fresh one.
                        requestActiveLocationUpdates(context);
                    }
                })
                .addOnFailureListener(e -> {
                    // If getting last location fails, fall back to requesting a fresh one.
                    requestActiveLocationUpdates(context);
                });
    }


    @SuppressWarnings("MissingPermission")
    public void requestActiveLocationUpdates(Context context) {
        if (googleLocationCallback != null) {
            // A request is already in progress.
            return;
        }
        
        LocationRequest locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000)
                .setMinUpdateIntervalMillis(5000)
                .build();

        googleLocationCallback = new com.google.android.gms.location.LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                timeoutHandler.removeCallbacks(timeoutRunnable); // Cancel the timeout
                stopLocationUpdates(); // Stop updates to save battery

                Location lastLocation = locationResult.getLastLocation();
                if (lastLocation != null) {
                    if (appCallback != null) {
                        appCallback.onLocationReceived(lastLocation);
                    }
                } else {
                    if (appCallback != null) {
                        appCallback.onLocationError("Could not determine location from active search.");
                    }
                }
            }

            @Override
            public void onLocationAvailability(@NonNull LocationAvailability locationAvailability) {
                super.onLocationAvailability(locationAvailability);
                if (!locationAvailability.isLocationAvailable()) {
                    // This can be triggered if GPS signal is lost, but might not mean a permanent failure.
                    // The timeout is a more reliable way to handle "no location found".
                }
            }
        };

        // Set up a 15-second timeout for the location request.
        timeoutRunnable = () -> {
            stopLocationUpdates(); // Stop trying to get location
            if (appCallback != null) {
                appCallback.onLocationError("Location request timed out. Please try again outdoors.");
            }
        };
        timeoutHandler.postDelayed(timeoutRunnable, 15000); // 15 seconds

        fusedLocationClient.requestLocationUpdates(locationRequest, googleLocationCallback, Looper.getMainLooper());
    }

    public void stopLocationUpdates() {
        if (timeoutHandler != null && timeoutRunnable != null) {
            timeoutHandler.removeCallbacks(timeoutRunnable);
        }
        if (fusedLocationClient != null && googleLocationCallback != null) {
            fusedLocationClient.removeLocationUpdates(googleLocationCallback);
            googleLocationCallback = null;
        }
    }

    public int getLocationPermissionRequestCode() {
        return LOCATION_PERMISSION_REQUEST_CODE;
    }
}