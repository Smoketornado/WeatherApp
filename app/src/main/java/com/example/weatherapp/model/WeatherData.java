package com.example.weatherapp.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable; // Importieren Sie Serializable

/**
 * Model class representing weather data from OpenWeatherMap API
 * Contains only fields available in the free tier
 */
// Fügen Sie "implements Serializable" hinzu
public class WeatherData implements Serializable {
    // Es ist eine gute Praxis, eine serialVersionUID hinzuzufügen, wenn Sie Serializable verwenden.
    // Sie können eine beliebige long-Zahl nehmen oder sie von Ihrer IDE generieren lassen.
    private static final long serialVersionUID = 1L;

    @SerializedName("name")
    private String locationName;

    @SerializedName("main")
    private MainWeather main;

    @SerializedName("weather")
    private WeatherCondition[] weather; // Arrays von Serializable Objekten sind ebenfalls serialisierbar

    @SerializedName("wind")
    private Wind wind;

    @SerializedName("coord")
    private Coordinates coordinates;

    // Getters
    public String getLocationName() {
        return locationName;
    }

    public MainWeather getMain() {
        return main;
    }

    public WeatherCondition[] getWeather() {
        return weather;
    }

    public Wind getWind() {
        return wind;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    /**
     * Inner class for main weather data
     */
    // Auch innere Klassen müssen Serializable sein, wenn die äußere Klasse es ist
    // und sie als Felder verwendet werden.
    public static class MainWeather implements Serializable {
        private static final long serialVersionUID = 2L; // Eigene ID für diese Klasse

        @SerializedName("temp")
        private double temperature;

        @SerializedName("temp_min")
        private double tempMin;

        @SerializedName("temp_max")
        private double tempMax;

        @SerializedName("humidity")
        private int humidity;

        @SerializedName("pressure")
        private int pressure;

        // Getters
        public double getTemperature() {
            return temperature;
        }

        public double getTempMin() {
            return tempMin;
        }

        public double getTempMax() {
            return tempMax;
        }

        public int getHumidity() {
            return humidity;
        }

        public int getPressure() {
            return pressure;
        }
    }

    /**
     * Inner class for weather condition description
     */
    public static class WeatherCondition implements Serializable {
        private static final long serialVersionUID = 3L;

        @SerializedName("description")
        private String description;

        @SerializedName("icon")
        private String icon;

        public String getDescription() {
            return description;
        }

        public String getIcon() {
            return icon;
        }
    }

    /**
     * Inner class for wind data
     */
    public static class Wind implements Serializable {
        private static final long serialVersionUID = 4L;

        @SerializedName("speed")
        private double speed;

        public double getSpeed() {
            return speed;
        }
    }

    /**
     * Inner class for coordinates
     */
    public static class Coordinates implements Serializable {
        private static final long serialVersionUID = 5L;

        @SerializedName("lat")
        private double latitude;

        @SerializedName("lon")
        private double longitude;

        public double getLatitude() {
            return latitude;
        }

        public double getLongitude() {
            return longitude;
        }
    }
}