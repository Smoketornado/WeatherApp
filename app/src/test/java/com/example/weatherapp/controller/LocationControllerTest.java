package com.example.weatherapp.controller;

import android.location.Location;
import org.junit.Test;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import static org.junit.Assert.*;

public class LocationControllerTest {

    @Test
    public void testOnLocationReceivedCallback() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        LocationController.AppLocationCallback callback = new LocationController.AppLocationCallback() {
            @Override
            public void onLocationReceived(Location location) {
                latch.countDown();
            }
            @Override public void onLocationError(String error) {}
            @Override public void onPermissionDenied() {}
            @Override public void onLocationSettingsNeeded() {}
        };
        // Simuliere den Callback-Aufruf
        callback.onLocationReceived(new Location("test"));
        assertTrue(latch.await(1, TimeUnit.SECONDS));
    }

    @Test
    public void testOnLocationErrorCallback() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        LocationController.AppLocationCallback callback = new LocationController.AppLocationCallback() {
            @Override public void onLocationReceived(Location location) {}
            @Override
            public void onLocationError(String error) {
                latch.countDown();
            }
            @Override public void onPermissionDenied() {}
            @Override public void onLocationSettingsNeeded() {}
        };
        callback.onLocationError("Fehler");
        assertTrue(latch.await(1, TimeUnit.SECONDS));
    }


}