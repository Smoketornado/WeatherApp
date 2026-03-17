package com.example.weatherapp;

import android.content.Intent;
import android.content.pm.PackageManager; // Hinzugefügt für PackageManager.PERMISSION_GRANTED
import android.location.Location;
import android.net.Uri; // Hinzugefügt für App-Einstellungen Intent
import android.os.Bundle;
import android.provider.Settings; // Hinzugefügt für Standorteinstellungen Intent
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull; // Hinzugefügt
import androidx.annotation.Nullable; // Hinzugefügt
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.weatherapp.api.IWeatherService;
import com.example.weatherapp.api.WeatherServiceImpl;
import com.example.weatherapp.api.ApiClient;
import com.example.weatherapp.controller.ILocationService;
import com.example.weatherapp.controller.LocationServiceImpl;
import com.example.weatherapp.model.WeatherData;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.widget.EditText;
import android.location.Address;
import android.location.Geocoder;
import java.io.IOException;
import java.util.List;

/**
 * Main Activity - Displays current weather information
 * Implements MVC pattern with controllers for location and weather data
 */
public class MainActivity extends AppCompatActivity {

    // UI Components
    private TextView tvLocation, tvLastUpdated, tvTemperature, tvWeatherDescription;
    private TextView tvTempMin, tvTempMax, tvHumidity, tvPressure, tvWindSpeed;
    // Optional: Eine separate TextView für Statusmeldungen zum Standortabruf
    // private TextView tvLocationStatus;
    private FloatingActionButton fabRefresh;
    private FloatingActionButton fabSearch;
    private MaterialButton btnDetails, btnMap;

    // Services
    private ILocationService locationService;
    private IWeatherService weatherService;

    // Data
    private WeatherData currentWeatherData;
    private Location currentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize services
        locationService = new LocationServiceImpl(this);
        weatherService = new WeatherServiceImpl(ApiClient.getWeatherService(), "97daa9137d08631d85e7726426ab0665", "metric");

        // Initialize UI components
        initializeViews();
        setupClickListeners();

        // Start location and weather retrieval
        fetchLocationAndWeather();
    }

    private void initializeViews() {
        tvLocation = findViewById(R.id.tvLocation);
        tvLastUpdated = findViewById(R.id.tvLastUpdated);
        tvTemperature = findViewById(R.id.tvTemperature);
        tvWeatherDescription = findViewById(R.id.tvWeatherDescription);
        tvTempMin = findViewById(R.id.tvTempMin);
        tvTempMax = findViewById(R.id.tvTempMax);
        tvHumidity = findViewById(R.id.tvHumidity);
        tvPressure = findViewById(R.id.tvPressure);
        tvWindSpeed = findViewById(R.id.tvWindSpeed);
        fabRefresh = findViewById(R.id.fabRefresh);
        fabSearch = findViewById(R.id.fabSearch);
        btnDetails = findViewById(R.id.btnDetails);
        btnMap = findViewById(R.id.btnMap);
        // if (tvLocationStatus == null) tvLocationStatus = tvLocation; // Beispiel, falls Sie tvLocation für Status nutzen
    }

    private void setupClickListeners() {
        fabRefresh.setOnClickListener(v -> refreshWeatherData());
        fabSearch.setOnClickListener(v -> showManualLocationDialog());
        btnDetails.setOnClickListener(v -> openDetailsActivity());
        btnMap.setOnClickListener(v -> openMapActivity());
    }

    private void fetchLocationAndWeather() {
        locationService.getCurrentLocation(new ILocationService.LocationCallback() {
            @Override
            public void onSuccess(Location location) {
                Log.d("WeatherApp", "Standort erhalten: " + location);
                currentLocation = location;
                fetchWeather(location);
            }
            @Override
            public void onError(Throwable t) {
                Log.d("WeatherApp", "Standortfehler: " + t.getMessage());
                Toast.makeText(MainActivity.this, "Standortfehler: " + t.getMessage(), Toast.LENGTH_LONG).show();
                tvLocation.setText("Standort nicht verfügbar");
            }
        });
    }

    private void fetchWeather(Location location) {
        weatherService.getCurrentWeather(location.getLatitude(), location.getLongitude(), new IWeatherService.WeatherCallback() {
            @Override
            public void onSuccess(WeatherData data) {
                runOnUiThread(() -> updateWeatherUI(data));
            }
            @Override
            public void onError(Throwable t) {
                runOnUiThread(() -> {
                    Toast.makeText(MainActivity.this, "Wetterdatenfehler: " + t.getMessage(), Toast.LENGTH_LONG).show();
                    tvLocation.setText("Wetterdaten nicht verfügbar");
                });
            }
        });
    }

    private void refreshWeatherData() {
        locationService.forceActiveLocationUpdate(new ILocationService.LocationCallback() {
            @Override
            public void onSuccess(Location location) {
                currentLocation = location;
                fetchWeather(location);
            }
            @Override
            public void onError(Throwable t) {
                Toast.makeText(MainActivity.this, "Standortfehler: " + t.getMessage(), Toast.LENGTH_LONG).show();
                tvLocation.setText("Standort nicht verfügbar");
            }
        });
    }

    private void openDetailsActivity() {
        if (currentWeatherData != null) {
            Intent intent = new Intent(this, DetailsActivity.class);
            intent.putExtra("weather_data", currentWeatherData);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Keine Wetterdaten zum Anzeigen verfügbar.", Toast.LENGTH_SHORT).show();
        }
    }

    private void openMapActivity() {
        Intent intent = new Intent(this, MapActivity.class);
        if (currentLocation != null) {
            intent.putExtra("latitude", currentLocation.getLatitude());
            intent.putExtra("longitude", currentLocation.getLongitude());
        }
        if (currentWeatherData != null) {
            intent.putExtra("location_name", currentWeatherData.getLocationName());
        }
        startActivity(intent);
    }

    private void updateWeatherUI(WeatherData weatherData) {
        if (weatherData == null) {
            // tvLocation.setText("Fehler beim Laden der Wetterdaten."); // Status
            return;
        }
        currentWeatherData = weatherData;

        tvLocation.setText(weatherData.getLocationName());
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        tvLastUpdated.setText("Aktualisiert: " + sdf.format(new Date()));

        if (weatherData.getMain() != null) {
            tvTemperature.setText(weatherService.formatTemperature(weatherData.getMain().getTemperature()));
            tvTempMin.setText("Min: " + weatherService.formatTemperature(weatherData.getMain().getTempMin()));
            tvTempMax.setText("Max: " + weatherService.formatTemperature(weatherData.getMain().getTempMax()));
            tvHumidity.setText(weatherService.formatHumidity(weatherData.getMain().getHumidity()));
            tvPressure.setText(weatherService.formatPressure(weatherData.getMain().getPressure()));
        }

        if (weatherData.getWeather() != null && weatherData.getWeather().length > 0) {
            tvWeatherDescription.setText(weatherData.getWeather()[0].getDescription());
        }

        if (weatherData.getWind() != null) {
            tvWindSpeed.setText(weatherService.formatWindSpeed(weatherData.getWind().getSpeed()));
        }
    }

    // Zeigt einen Dialog zur manuellen Eingabe eines Standorts
    private void showManualLocationDialog() {
        EditText input = new EditText(this);
        input.setHint("Stadt, Adresse oder Koordinaten");
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Standort eingeben")
                .setMessage("Bitte gib einen Ort, eine Adresse oder Koordinaten ein:")
                .setView(input)
                .setPositiveButton("OK", (dialog, which) -> {
                    String entered = input.getText().toString().trim();
                    if (!entered.isEmpty()) {
                        // Geocoding im Hintergrund
                        new Thread(() -> {
                            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                            try {
                                List<Address> addresses = geocoder.getFromLocationName(entered, 1);
                                if (addresses != null && !addresses.isEmpty()) {
                                    Address address = addresses.get(0);
                                    double lat = address.getLatitude();
                                    double lon = address.getLongitude();
                                    // Erstelle neue Location und überschreibe currentLocation
                                    Location manualLocation = new Location("manual");
                                    manualLocation.setLatitude(lat);
                                    manualLocation.setLongitude(lon);
                                    manualLocation.setTime(System.currentTimeMillis());
                                    runOnUiThread(() -> {
                                        currentLocation = manualLocation;
                                        tvLocation.setText(address.getFeatureName() != null ? address.getFeatureName() : entered);
                                        fetchWeather(currentLocation);
                                        Toast.makeText(this, "Standort übernommen: " + entered, Toast.LENGTH_SHORT).show();
                                    });
                                } else {
                                    runOnUiThread(() -> Toast.makeText(this, "Ort nicht gefunden!", Toast.LENGTH_LONG).show());
                                }
                            } catch (IOException e) {
                                runOnUiThread(() -> Toast.makeText(this, "Geocoding-Fehler: " + e.getMessage(), Toast.LENGTH_LONG).show());
                            }
                        }).start();
                    } else {
                        Toast.makeText(this, "Bitte gib einen gültigen Standort ein!", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Abbrechen", null)
                .show();
    }

    // --- Activity Lifecycle und Permission Results ---
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == locationService.getLocationPermissionRequestCode()) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Berechtigung erteilt, jetzt Geräteeinstellungen prüfen
                // tvLocation.setText("Berechtigung erteilt. Prüfe Geräteeinstellungen..."); // Status
                locationService.checkDeviceLocationSettings(this);
            } else {
                onPermissionDenied(); // Ruft Ihre Implementierung von onPermissionDenied auf
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == locationService.REQUEST_CHECK_SETTINGS) {
            // Nutzer kehrt von den Standorteinstellungen zurück.
            // Es gibt keinen garantierten resultCode, also prüfen wir einfach erneut.
            // tvLocation.setText("Prüfe Geräteeinstellungen erneut..."); // Status
            locationService.checkDeviceLocationSettings(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Optionale Logik für onResume:
        // Wenn Sie möchten, dass die Daten immer aktualisiert werden, wenn die App in den Vordergrund kommt,
        // und ein Standort bereits bekannt ist:
        if (currentLocation != null) {
            // tvLocation.setText("Aktualisiere Wetterdaten..."); // Status
            fetchWeather(currentLocation);
        } else {
            // Wenn kein Standort bekannt ist (z.B. beim ersten Start oder wenn es vorher fehlschlug),
            // könnte man den Prozess hier erneut anstoßen, aber das geschieht schon in onCreate.
            // locationService.initiateLocationRetrieval(this); // Nur wenn nicht schon in onCreate passiert
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        locationService.stopLocationUpdates(); // Wichtig: Updates stoppen
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        locationService.stopLocationUpdates(); // Sicherstellen, dass Updates gestoppt sind
    }

    private void onPermissionDenied() {
        tvLocation.setText("Berechtigung verweigert"); // UI entsprechend aktualisieren
        new AlertDialog.Builder(this)
                .setTitle("Standortberechtigung erforderlich")
                .setMessage("Diese App benötigt Zugriff auf Ihren Standort, um das Wetter an Ihrem aktuellen Ort anzuzeigen. Bitte erteilen Sie die Berechtigung in den App-Einstellungen.")
                .setPositiveButton("Zu den Einstellungen", (dialog, which) -> {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                })
                .setNegativeButton("Abbrechen", (dialog, which) ->
                        Toast.makeText(MainActivity.this, "Ohne Standortberechtigung können keine lokalen Wetterdaten abgerufen werden.", Toast.LENGTH_LONG).show()
                )
                .show();
    }
}