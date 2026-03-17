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
import static androidx.test.espresso.action.ViewActions.click;
import static org.hamcrest.Matchers.anyOf;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class MapActivityTest {

    @Test
    public void testMapActivityDisplaysMapAndLocation() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), MapActivity.class);
        intent.putExtra("location_name", "Teststadt");
        intent.putExtra("latitude", 51.0);
        intent.putExtra("longitude", 13.0);

        try (ActivityScenario<MapActivity> scenario = ActivityScenario.launch(intent)) {
            onView(withId(R.id.mapView)).check(matches(isDisplayed()));
            onView(withId(R.id.tvMapLocation)).check(matches(withText("Teststadt")));
            onView(withId(R.id.fabBackToMain)).check(matches(isDisplayed()));
            onView(withId(R.id.fabCenterLocation)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void testBackButtonIsClickable() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), MapActivity.class);
        intent.putExtra("location_name", "Teststadt");
        try (ActivityScenario<MapActivity> scenario = ActivityScenario.launch(intent)) {
            onView(withId(R.id.fabBackToMain)).perform(click());
            // Wenn kein Fehler: Test bestanden
        }
    }

    @Test
    public void testCenterLocationButtonIsClickable() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), MapActivity.class);
        intent.putExtra("location_name", "Teststadt");
        intent.putExtra("latitude", 51.0);
        intent.putExtra("longitude", 13.0);
        try (ActivityScenario<MapActivity> scenario = ActivityScenario.launch(intent)) {
            onView(withId(R.id.fabCenterLocation)).perform(click());
            // Wenn kein Fehler: Test bestanden
        }
    }

    @Test
    public void testNoLocationShowsUnavailableText() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), MapActivity.class);
        // Keine Location-Extras setzen
        try (ActivityScenario<MapActivity> scenario = ActivityScenario.launch(intent)) {
            onView(withId(R.id.tvMapLocation)).check(matches(anyOf(
                withText("Ort nicht lokalisierbar"),
                withText("Ort wird geladen...")
            )));
        }
    }
} 