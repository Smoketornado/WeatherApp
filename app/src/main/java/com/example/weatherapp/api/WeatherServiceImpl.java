package com.example.weatherapp.api;

import com.example.weatherapp.model.WeatherData;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WeatherServiceImpl implements IWeatherService {
    private final WeatherApiService apiService;
    private final String apiKey;
    private final String units;

    public WeatherServiceImpl(WeatherApiService apiService, String apiKey, String units) {
        this.apiService = apiService;
        this.apiKey = apiKey;
        this.units = units;
    }

    @Override
    public void getCurrentWeather(double lat, double lon, WeatherCallback callback) {
        Call<WeatherData> call = apiService.getCurrentWeather(lat, lon, apiKey, units);
        call.enqueue(new Callback<WeatherData>() {
            @Override
            public void onResponse(Call<WeatherData> call, Response<WeatherData> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError(new Exception("HTTP " + response.code() + ": " + response.message()));
                }
            }
            @Override
            public void onFailure(Call<WeatherData> call, Throwable t) {
                callback.onError(t);
            }
        });
    }

    @Override
    public String formatTemperature(double temperature) {
        return String.format("%.1f°C", temperature);
    }

    @Override
    public String formatWindSpeed(double windSpeed) {
        return String.format("%.1f m/s", windSpeed);
    }

    @Override
    public String formatHumidity(int humidity) {
        return String.format("%d%%", humidity);
    }

    @Override
    public String formatPressure(int pressure) {
        return String.format("%d hPa", pressure);
    }
} 