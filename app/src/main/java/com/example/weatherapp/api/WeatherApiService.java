package com.example.weatherapp.api;

import com.example.weatherapp.model.WeatherData;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Retrofit interface for OpenWeatherMap API calls
 * Uses only the free tier endpoints
 */
public interface WeatherApiService {

    /**
     * Get current weather data by coordinates
     * @param lat Latitude
     * @param lon Longitude
     * @param apiKey OpenWeatherMap API key
     * @param units Units (metric, imperial, kelvin)
     * @return WeatherData object
     */
    @GET("weather")
    Call<WeatherData> getCurrentWeather(
        @Query("lat") double lat,
        @Query("lon") double lon,
        @Query("appid") String apiKey,
        @Query("units") String units
    );

    /**
     * Get current weather data by city name
     * @param cityName City name
     * @param apiKey OpenWeatherMap API key
     * @param units Units (metric, imperial, kelvin)
     * @return WeatherData object
     */
    @GET("weather")
    Call<WeatherData> getCurrentWeatherByCity(
        @Query("q") String cityName,
        @Query("appid") String apiKey,
        @Query("units") String units
    );
}