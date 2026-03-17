package com.example.weatherapp;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

/**
 * Instrumentation tests for MainActivity
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class MainActivityTest {

    @Rule
    public ActivityScenarioRule<MainActivity> activityScenarioRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void testMainActivityLaunches() {
        // Verify that the main activity launches and displays
        onView(withId(R.id.tvLocation)).check(matches(isDisplayed()));
        onView(withId(R.id.tvTemperature)).check(matches(isDisplayed()));
        onView(withId(R.id.fabRefresh)).check(matches(isDisplayed()));
    }

    @Test
    public void testRefreshButtonIsDisplayed() {
        // Verify refresh button is visible
        onView(withId(R.id.fabRefresh)).check(matches(isDisplayed()));
    }

    @Test
    public void testDetailsButtonIsDisplayed() {
        // Verify details button is visible
        onView(withId(R.id.btnDetails)).check(matches(isDisplayed()));
    }

    @Test
    public void testMapButtonIsDisplayed() {
        // Verify map button is visible
        onView(withId(R.id.btnMap)).check(matches(isDisplayed()));
    }

    @Test
    public void testWeatherInformationFieldsAreDisplayed() {
        // Verify all weather information fields are displayed
        onView(withId(R.id.tvTemperature)).check(matches(isDisplayed()));
        onView(withId(R.id.tvWeatherDescription)).check(matches(isDisplayed()));
        onView(withId(R.id.tvTempMin)).check(matches(isDisplayed()));
        onView(withId(R.id.tvTempMax)).check(matches(isDisplayed()));
        onView(withId(R.id.tvHumidity)).check(matches(isDisplayed()));
        onView(withId(R.id.tvPressure)).check(matches(isDisplayed()));
        onView(withId(R.id.tvWindSpeed)).check(matches(isDisplayed()));
    }

    @Test
    public void testRefreshButtonIsClickable() {
        // Verify refresh button can be clicked (doesn't crash)
        onView(withId(R.id.fabRefresh)).perform(click());
        // If no exception is thrown, the button is clickable
    }

    @Test
    public void testDetailsButtonIsClickable() {
        // Verify details button can be clicked (doesn't crash)
        onView(withId(R.id.btnDetails)).perform(click());
        // If no exception is thrown, the button is clickable
    }

    @Test
    public void testMapButtonIsClickable() {
        // Verify map button can be clicked (doesn't crash)
        onView(withId(R.id.btnMap)).perform(click());
        // If no exception is thrown, the button is clickable
    }

    @Test
    public void testInitialWeatherDisplay() {
        // Verify initial weather display shows placeholder or loading text
        onView(withId(R.id.tvTemperature)).check(matches(isDisplayed()));
        onView(withId(R.id.tvLastUpdated)).check(matches(isDisplayed()));
    }

    @Test
    public void testLocationDisplay() {
        // Verify location display is shown
        onView(withId(R.id.tvLocation)).check(matches(isDisplayed()));
    }
} 