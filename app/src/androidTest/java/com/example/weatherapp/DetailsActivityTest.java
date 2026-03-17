package com.example.weatherapp;

import android.content.Intent;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import com.example.weatherapp.model.WeatherData;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class DetailsActivityTest {

    private WeatherData createDummyWeatherData() {
        WeatherData data = new WeatherData();
        setField(data, "locationName", "Teststadt");

        WeatherData.MainWeather main = new WeatherData.MainWeather();
        setField(main, "temperature", 22.5);
        setField(main, "tempMin", 18.0);
        setField(main, "tempMax", 25.0);
        setField(main, "humidity", 60);
        setField(main, "pressure", 1012);
        setField(data, "main", main);

        WeatherData.WeatherCondition[] conditions = new WeatherData.WeatherCondition[1];
        WeatherData.WeatherCondition cond = new WeatherData.WeatherCondition();
        setField(cond, "description", "Leicht bewölkt");
        setField(cond, "icon", "02d");
        conditions[0] = cond;
        setField(data, "weather", conditions);

        WeatherData.Wind wind = new WeatherData.Wind();
        setField(wind, "speed", 5.2);
        setField(data, "wind", wind);

        WeatherData.Coordinates coords = new WeatherData.Coordinates();
        setField(coords, "latitude", 51.0504);
        setField(coords, "longitude", 13.7373);
        setField(data, "coordinates", coords);

        return data;
    }

    private void setField(Object obj, String fieldName, Object value) {
        try {
            java.lang.reflect.Field field = obj.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(obj, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testDetailsActivityDisplaysWeatherData() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), DetailsActivity.class);
        intent.putExtra("weather_data", createDummyWeatherData());
        try (ActivityScenario<DetailsActivity> scenario = ActivityScenario.launch(intent)) {
            onView(withId(R.id.tvDetailsLocation)).check(matches(withText("Teststadt")));
            onView(withId(R.id.tvDetailsTemperature)).check(matches(isDisplayed()));
            onView(withId(R.id.tvDetailsTempMin)).check(matches(isDisplayed()));
            onView(withId(R.id.tvDetailsTempMax)).check(matches(isDisplayed()));
            onView(withId(R.id.tvDetailsWeatherDescription)).check(matches(isDisplayed()));
            onView(withId(R.id.tvDetailsHumidity)).check(matches(isDisplayed()));
            onView(withId(R.id.tvDetailsPressure)).check(matches(isDisplayed()));
            onView(withId(R.id.tvDetailsWindSpeed)).check(matches(isDisplayed()));
            onView(withId(R.id.tvDetailsLatitude)).check(matches(isDisplayed()));
            onView(withId(R.id.tvDetailsLongitude)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void testBackButtonIsClickable() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), DetailsActivity.class);
        intent.putExtra("weather_data", createDummyWeatherData());
        try (ActivityScenario<DetailsActivity> scenario = ActivityScenario.launch(intent)) {
            onView(withId(R.id.btnBack)).check(matches(isDisplayed()));
            onView(withId(R.id.btnBack)).perform(androidx.test.espresso.action.ViewActions.click());
        }
    }
} 