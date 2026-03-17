package com.example.weatherapp.api;

import com.example.weatherapp.model.WeatherData;
// ForecastData und andere Modelle ggf. anlegen, falls benötigt

public interface IWeatherService {
    interface WeatherCallback {
        void onSuccess(WeatherData data);
        void onError(Throwable t);
    }
    void getCurrentWeather(double lat, double lon, WeatherCallback callback);
    // ForecastData getForecast(double lat, double lon);
    // double getUVIndex(double lat, double lon);
    String formatTemperature(double temperature);
    String formatWindSpeed(double windSpeed);
    String formatHumidity(int humidity);
    String formatPressure(int pressure);
} 