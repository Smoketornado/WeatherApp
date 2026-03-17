package com.example.weatherapp;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.weatherapp.model.WeatherData;
import com.google.android.material.button.MaterialButton;

import com.example.weatherapp.api.IWeatherService;
import com.example.weatherapp.api.WeatherServiceImpl;
import com.example.weatherapp.api.ApiClient;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.example.weatherapp.controller.LocationServiceImpl;

/**
 * Details Activity - Displays detailed weather information
 * Shows expanded view of all weather data
 */
public class DetailsActivity extends AppCompatActivity {

    // UI Components
    private TextView tvDetailsLocation, tvDetailsLastUpdated;
    private TextView tvDetailsTemperature, tvDetailsTempMin, tvDetailsTempMax;
    private TextView tvDetailsWeatherDescription;
    private TextView tvDetailsHumidity, tvDetailsPressure, tvDetailsWindSpeed;
    private TextView tvDetailsLatitude, tvDetailsLongitude;
    private MaterialButton btnBack;

    // Service
    private IWeatherService weatherService;

    // Data
    private WeatherData weatherData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        // Initialize service
        weatherService = new WeatherServiceImpl(ApiClient.getWeatherService(), "97daa9137d08631d85e7726426ab0665", "metric");

        // Initialize UI components
        initializeViews();
        setupClickListeners();

        // Get weather data from intent
        getWeatherDataFromIntent();

        // Initialize location service
        LocationServiceImpl locationService = new LocationServiceImpl(this);
    }

    /**
     * Initialize UI components
     */
    private void initializeViews() {
        tvDetailsLocation = findViewById(R.id.tvDetailsLocation);
        tvDetailsLastUpdated = findViewById(R.id.tvDetailsLastUpdated);
        tvDetailsTemperature = findViewById(R.id.tvDetailsTemperature);
        tvDetailsTempMin = findViewById(R.id.tvDetailsTempMin);
        tvDetailsTempMax = findViewById(R.id.tvDetailsTempMax);
        tvDetailsWeatherDescription = findViewById(R.id.tvDetailsWeatherDescription);
        tvDetailsHumidity = findViewById(R.id.tvDetailsHumidity);
        tvDetailsPressure = findViewById(R.id.tvDetailsPressure);
        tvDetailsWindSpeed = findViewById(R.id.tvDetailsWindSpeed);
        tvDetailsLatitude = findViewById(R.id.tvDetailsLatitude);
        tvDetailsLongitude = findViewById(R.id.tvDetailsLongitude);
        btnBack = findViewById(R.id.btnBack);
    }

    /**
     * Setup click listeners
     */
    private void setupClickListeners() {
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**
     * Get weather data from intent and update UI
     */
    private void getWeatherDataFromIntent() {
        // Retrieve the WeatherData object from the intent.
        // This is possible because you made the WeatherData class Serializable.
        weatherData = (WeatherData) getIntent().getSerializableExtra("weather_data");

        if (weatherData != null) {
            // If data is received, update the UI with it.
            updateWeatherUI(weatherData);
        } else {
            // If no data is found, show an error and close the activity.
            Toast.makeText(this, "Could not load weather details.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    /**
     * Update UI with actual weather data
     * This method would be called when receiving actual WeatherData object
     */
    private void updateWeatherUI(WeatherData weatherData) {
        if (weatherData == null) return;

        this.weatherData = weatherData;

        // Update location
        tvDetailsLocation.setText(weatherData.getLocationName());

        // Update last updated time
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        tvDetailsLastUpdated.setText("Last updated: " + sdf.format(new Date()));

        // Update temperature information
        if (weatherData.getMain() != null) {
            tvDetailsTemperature.setText(weatherService.formatTemperature(weatherData.getMain().getTemperature()));
            tvDetailsTempMin.setText(weatherService.formatTemperature(weatherData.getMain().getTempMin()));
            tvDetailsTempMax.setText(weatherService.formatTemperature(weatherData.getMain().getTempMax()));
            tvDetailsHumidity.setText(weatherService.formatHumidity(weatherData.getMain().getHumidity()));
            tvDetailsPressure.setText(weatherService.formatPressure(weatherData.getMain().getPressure()));
        }

        // Update weather description
        if (weatherData.getWeather() != null && weatherData.getWeather().length > 0) {
            tvDetailsWeatherDescription.setText(weatherData.getWeather()[0].getDescription());
        }

        // Update wind speed
        if (weatherData.getWind() != null) {
            tvDetailsWindSpeed.setText(weatherService.formatWindSpeed(weatherData.getWind().getSpeed()));
        }

        // Update coordinates
        if (weatherData.getCoordinates() != null) {
            tvDetailsLatitude.setText(String.format(Locale.getDefault(), "%.6f", weatherData.getCoordinates().getLatitude()));
            tvDetailsLongitude.setText(String.format(Locale.getDefault(), "%.6f", weatherData.getCoordinates().getLongitude()));
        }
    }
} 