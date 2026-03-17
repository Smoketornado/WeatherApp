package com.example.weatherapp.controller;

import com.example.weatherapp.api.ApiClient;
import com.example.weatherapp.api.WeatherApiService;
import com.example.weatherapp.model.WeatherData;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Controller class for weather data operations
 * Handles API calls and data processing
 */
public class WeatherController {
    private static final String API_KEY = "97daa9137d08631d85e7726426ab0665"; // Mein API Key
    private static final String UNITS = "metric"; // Einheit auf Metrisch
    
    private WeatherApiService weatherService;
    private WeatherDataCallback callback;
    
    public interface WeatherDataCallback {
        void onWeatherDataReceived(WeatherData weatherData);
        void onWeatherDataError(String error);
    }
    
    public WeatherController() {
        this.weatherService = ApiClient.getWeatherService();
    }
    
    /**
     * Set callback for weather data updates
     * @param callback WeatherDataCallback implementation
     */
    public void setCallback(WeatherDataCallback callback) {
        this.callback = callback;
    }
    
    /**
     * Fetch weather data by coordinates
     * @param latitude Latitude
     * @param longitude Longitude
     */
    public void fetchWeatherData(double latitude, double longitude) {
        Call<WeatherData> call = weatherService.getCurrentWeather(latitude, longitude, API_KEY, UNITS);
        
        call.enqueue(new Callback<WeatherData>() {
            @Override
            public void onResponse(Call<WeatherData> call, Response<WeatherData> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (callback != null) {
                        callback.onWeatherDataReceived(response.body());
                    }
                } else {
                    if (callback != null) {
                        String errorBody = "No error body";
                        try {
                            if (response.errorBody() != null) {
                                errorBody = response.errorBody().string();
                            }
                        } catch (Exception e) {
                            // Ignore
                        }
                        callback.onWeatherDataError("HTTP " + response.code() + " " + response.message() + ": " + errorBody);
                    }
                }
            }
            
            @Override
            public void onFailure(Call<WeatherData> call, Throwable t) {
                if (callback != null) {
                    callback.onWeatherDataError("Network error: " + t.getMessage());
                }
            }
        });
    }
    
    /**
     * Fetch weather data by city name
     * @param cityName City name
     */
    public void fetchWeatherDataByCity(String cityName) {
        Call<WeatherData> call = weatherService.getCurrentWeatherByCity(cityName, API_KEY, UNITS);
        
        call.enqueue(new Callback<WeatherData>() {
            @Override
            public void onResponse(Call<WeatherData> call, Response<WeatherData> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (callback != null) {
                        callback.onWeatherDataReceived(response.body());
                    }
                } else {
                    if (callback != null) {
                        String errorBody = "No error body";
                        try {
                            if (response.errorBody() != null) {
                                errorBody = response.errorBody().string();
                            }
                        } catch (Exception e) {
                            // Ignore
                        }
                        callback.onWeatherDataError("HTTP " + response.code() + " " + response.message() + ": " + errorBody);
                    }
                }
            }
            
            @Override
            public void onFailure(Call<WeatherData> call, Throwable t) {
                if (callback != null) {
                    callback.onWeatherDataError("Network error: " + t.getMessage());
                }
            }
        });
    }
    
    /**
     * Format temperature for display
     * @param temperature Temperature in Kelvin
     * @return Formatted temperature string
     */
    public String formatTemperature(double temperature) {
        return String.format("%.1f°C", temperature);
    }
    
    /**
     * Format wind speed for display
     * @param windSpeed Wind speed in m/s
     * @return Formatted wind speed string
     */
    public String formatWindSpeed(double windSpeed) {
        return String.format("%.1f m/s", windSpeed);
    }
    
    /**
     * Format humidity for display
     * @param humidity Humidity percentage
     * @return Formatted humidity string
     */
    public String formatHumidity(int humidity) {
        return String.format("%d%%", humidity);
    }
    
    /**
     * Format pressure for display
     * @param pressure Pressure in hPa
     * @return Formatted pressure string
     */
    public String formatPressure(int pressure) {
        return String.format("%d hPa", pressure);
    }
} 