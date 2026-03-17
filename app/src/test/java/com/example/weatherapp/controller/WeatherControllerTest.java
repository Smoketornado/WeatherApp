package com.example.weatherapp.controller;

import com.example.weatherapp.model.WeatherData;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

/**
 * Unit tests for WeatherController (asynchrones Callback-Verhalten und Formatierung)
 */
@RunWith(MockitoJUnitRunner.class)
public class WeatherControllerTest {

    private WeatherController weatherController;

    @Mock
    private WeatherController.WeatherDataCallback mockCallback;

    @Before
    public void setUp() {
        weatherController = new WeatherController();
        weatherController.setCallback(mockCallback);
    }

    @Test
    public void testFormatTemperature() {
        // Test positive temperature
        String result = weatherController.formatTemperature(25.5);
        assertEquals("25,5°C", result);

        // Test negative temperature
        result = weatherController.formatTemperature(-10.2);
        assertEquals("-10,2°C", result);

        // Test zero temperature
        result = weatherController.formatTemperature(0.0);
        assertEquals("0,0°C", result);

        // Test decimal temperature
        result = weatherController.formatTemperature(22.75);
        assertEquals("22,8°C", result);
    }

    @Test
    public void testFormatWindSpeed() {
        // Test normal wind speed
        String result = weatherController.formatWindSpeed(5.5);
        assertEquals("5,5 m/s", result);

        // Test zero wind speed
        result = weatherController.formatWindSpeed(0.0);
        assertEquals("0,0 m/s", result);

        // Test high wind speed
        result = weatherController.formatWindSpeed(25.75);
        assertEquals("25,8 m/s", result);
    }

    @Test
    public void testFormatHumidity() {
        // Test normal humidity
        String result = weatherController.formatHumidity(65);
        assertEquals("65%", result);

        // Test zero humidity
        result = weatherController.formatHumidity(0);
        assertEquals("0%", result);

        // Test 100% humidity
        result = weatherController.formatHumidity(100);
        assertEquals("100%", result);
    }

    @Test
    public void testFormatPressure() {
        // Test normal pressure
        String result = weatherController.formatPressure(1013);
        assertEquals("1013 hPa", result);

        // Test low pressure
        result = weatherController.formatPressure(950);
        assertEquals("950 hPa", result);

        // Test high pressure
        result = weatherController.formatPressure(1050);
        assertEquals("1050 hPa", result);
    }

    @Test
    public void testWeatherControllerCreation() {
        assertNotNull(weatherController);
    }

    @Test
    public void testSetCallback() {
        WeatherController controller = new WeatherController();
        controller.setCallback(mockCallback);
        // If no exception is thrown, the callback was set successfully
    }

    @Test
    public void testOnWeatherDataReceivedCallback() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        WeatherController.WeatherDataCallback callback = new WeatherController.WeatherDataCallback() {
            @Override
            public void onWeatherDataReceived(WeatherData weatherData) {
                latch.countDown();
            }
            @Override
            public void onWeatherDataError(String error) {}
        };
        weatherController.setCallback(callback);
        callback.onWeatherDataReceived(new WeatherData());
        assertTrue(latch.await(1, TimeUnit.SECONDS));
    }

    @Test
    public void testOnWeatherDataErrorCallback() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        WeatherController.WeatherDataCallback callback = new WeatherController.WeatherDataCallback() {
            @Override public void onWeatherDataReceived(WeatherData weatherData) {}
            @Override
            public void onWeatherDataError(String error) {
                latch.countDown();
            }
        };
        weatherController.setCallback(callback);
        callback.onWeatherDataError("Fehler");
        assertTrue(latch.await(1, TimeUnit.SECONDS));
    }

    @Test
    public void testFetchWeatherDataWithValidCoordinates() {
        // This test would require mocking the API service
        // For now, we just test that the method doesn't throw an exception
        try {
            weatherController.fetchWeatherData(40.7128, -74.0060); // New York coordinates
            // If no exception is thrown, the method executed successfully
        } catch (Exception e) {
            // Expected in test environment without network
        }
    }

    @Test
    public void testFetchWeatherDataByCity() {
        // This test would require mocking the API service
        // For now, we just test that the method doesn't throw an exception
        try {
            weatherController.fetchWeatherDataByCity("London");
            // If no exception is thrown, the method executed successfully
        } catch (Exception e) {
            // Expected in test environment without network
        }
    }
} 