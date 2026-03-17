package com.example.weatherapp;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull; // Import NonNull
import androidx.annotation.Nullable; // Import Nullable
import androidx.appcompat.app.AppCompatActivity;

import com.example.weatherapp.controller.ILocationService;
import com.example.weatherapp.controller.LocationServiceImpl;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Map Activity - Displays user location on OpenStreetMap
 * Uses osmdroid for map functionality
 */
public class MapActivity extends AppCompatActivity {

    // UI Components
    private MapView mapView;
    private TextView tvMapLocation;
    private MaterialButton fabBackToMain;
    private FloatingActionButton fabCenterLocation;

    // Controller
    private ILocationService locationService;

    // Data
    private Location currentLocation;
    private Marker locationMarker;
    private String intentLocationName; // Store location name from intent

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize osmdroid configuration - MUST be before setContentView
        Configuration.getInstance().load(getApplicationContext(),
                getSharedPreferences("osmdroid", MODE_PRIVATE));
        Configuration.getInstance().setUserAgentValue(getPackageName());

        setContentView(R.layout.activity_map);

        // Initialize controller
        locationService = new LocationServiceImpl(this);

        // Initialize UI components
        initializeViews();
        setupClickListeners();

        // Get data from intent FIRST, as it might provide an initial location
        getDataFromIntent();

        // Setup map
        setupMap(); // setupMap needs to be called after mapView is initialized

        // If data from intent provided a location, use it. Otherwise, fetch current.
        if (currentLocation == null) {
            fetchLocation();
        } else {
            updateLocationDisplay();
            centerOnLocation();
        }
        // Timeout: Nach 10 Sekunden aufgeben, falls keine Location gefunden
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (currentLocation == null) {
                tvMapLocation.setText("Ort nicht lokalisierbar");
            }
        }, 10000); // 10 Sekunden
    }

    private void initializeViews() {
        mapView = findViewById(R.id.mapView);
        tvMapLocation = findViewById(R.id.tvMapLocation);
        fabBackToMain = findViewById(R.id.fabBackToMain);
        fabCenterLocation = findViewById(R.id.fabCenterLocation);
    }

    private void setupClickListeners() {
        fabBackToMain.setOnClickListener(v -> finish());
        fabCenterLocation.setOnClickListener(v -> centerOnLocation());
    }

    private void getDataFromIntent() {
        intentLocationName = getIntent().getStringExtra("location_name");
        if (intentLocationName != null) {
            tvMapLocation.setText(intentLocationName);
        }

        if (getIntent().hasExtra("latitude") && getIntent().hasExtra("longitude")) {
            double latitude = getIntent().getDoubleExtra("latitude", 0.0);
            double longitude = getIntent().getDoubleExtra("longitude", 0.0);

            if (latitude != 0.0 || longitude != 0.0) { // Check if either is non-zero
                currentLocation = new Location("intent"); // Provider name
                currentLocation.setLatitude(latitude);
                currentLocation.setLongitude(longitude);
                // Don't call updateLocationDisplay or centerOnLocation yet, map might not be ready.
                // This will be handled in onCreate after setupMap()
            }
        }
    }

    private void setupMap() {
        if (mapView == null) return; // Should not happen if called correctly
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setMultiTouchControls(true);
        mapView.setZoomRounding(true);
        mapView.getController().setZoom(10.0); // Initial zoom
        // Center on (0,0) initially, will be updated
        mapView.getController().setCenter(new GeoPoint(0.0, 0.0));
    }

    private void centerOnLocation() {
        if (mapView != null && currentLocation != null) {
            GeoPoint point = new GeoPoint(currentLocation.getLatitude(), currentLocation.getLongitude());
            mapView.getController().animateTo(point);
            // Only set zoom if it's a new focus, allow user to zoom manually otherwise
            if (mapView.getZoomLevelDouble() < 14.0) { // Or some other threshold
                mapView.getController().setZoom(15.0);
            }
        } else if (mapView != null) { // currentLocation is null
            Toast.makeText(this, "Location not yet available to center.", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateLocationMarker() {
        if (mapView == null || currentLocation == null) return;

        if (locationMarker != null) {
            mapView.getOverlays().remove(locationMarker);
        }

        locationMarker = new Marker(mapView);
        locationMarker.setPosition(new GeoPoint(currentLocation.getLatitude(), currentLocation.getLongitude()));
        // Use intentLocationName if available, otherwise a generic title or coordinates
        String title = intentLocationName != null ? intentLocationName : "Current Location";
        locationMarker.setTitle(title);
        // locationMarker.setSnippet("More details..."); // Optional snippet
        locationMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        // locationMarker.setIcon(getResources().getDrawable(R.drawable.ic_your_marker_icon, getTheme())); // Custom icon

        mapView.getOverlays().add(locationMarker);
        mapView.invalidate(); // Redraw the map
    }

    private void updateLocationDisplay() {
        if (currentLocation != null) {
            if (intentLocationName == null) {
                getAddressForLocation(currentLocation.getLatitude(), currentLocation.getLongitude());
            } else {
                tvMapLocation.setText(intentLocationName);
            }
            updateLocationMarker();
        } else {
            tvMapLocation.setText("Ort nicht lokalisierbar");
        }
    }

    // Holt den Ortsnamen asynchron und setzt ihn in tvMapLocation
    private void getAddressForLocation(double latitude, double longitude) {
        new Thread(() -> {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            String addressText = "Unbekannter Ort";
            try {
                List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                if (addresses != null && !addresses.isEmpty()) {
                    Address address = addresses.get(0);
                    StringBuilder sb = new StringBuilder();
                    if (address.getThoroughfare() != null) {
                        sb.append(address.getThoroughfare());
                        if (address.getSubThoroughfare() != null) {
                            sb.append(" ").append(address.getSubThoroughfare());
                        }
                        sb.append(", ");
                    }
                    if (address.getLocality() != null) {
                        sb.append(address.getLocality());
                    } else if (address.getAdminArea() != null) {
                        sb.append(address.getAdminArea());
                    }
                    addressText = sb.length() > 0 ? sb.toString() : "Unbekannter Ort";
                }
            } catch (IOException e) {
                addressText = "Ort nicht bestimmbar";
            }
            final String finalAddress = addressText;
            runOnUiThread(() -> tvMapLocation.setText(finalAddress));
        }).start();
    }

    private void fetchLocation() {
        locationService.getCurrentLocation(new ILocationService.LocationCallback() {
            @Override
            public void onSuccess(Location location) {
                currentLocation = location;
                intentLocationName = null;
                updateLocationDisplay();
                centerOnLocation();
                Toast.makeText(MapActivity.this, "Live location updated.", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onError(Throwable t) {
                Toast.makeText(MapActivity.this, "Map Location Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                tvMapLocation.setText("Error: " + t.getMessage());
            }
        });
    }

    // --- Activity Lifecycle & Permission/Activity Results ---
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // No permissions to check for this activity
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // No activity results to handle
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Required for osmdroid
        if (mapView != null) {
            mapView.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Required for osmdroid
        if (mapView != null) {
            mapView.onPause();
        }
        locationService.stopLocationUpdates(); // Stop location updates to save battery
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Required for osmdroid to release resources
        if (mapView != null) {
            mapView.onDetach();
        }
        locationService.stopLocationUpdates(); // Ensure updates are stopped
        mapView = null; // Help GC
        locationService = null; // Help GC
    }
}