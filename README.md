# WeatherApp 🌤️

A modern Android weather application built with Java following the MVC (Model-View-Controller) architecture.
The app fetches real-time weather data from the OpenWeatherMap API and visualizes the user's location using OpenStreetMap.

---

## 📱 Features

* 🌡️ Current weather data (temperature, min/max, humidity, pressure, wind speed)
* 📍 Location-based weather using GPS (FusedLocationProviderClient)
* 🗺️ Interactive map with OpenStreetMap (osmdroid)
* 📊 Detailed weather screen with extended information
* 🔄 Refresh functionality for real-time updates
* 🧪 Unit tests for controllers and models

---

## 🧠 Architecture

This project follows the **MVC pattern**:

* **Model:**
  Handles data representation (`WeatherData.java`)

* **View:**
  UI layer with Activities and XML layouts
  (`MainActivity`, `DetailsActivity`, `MapActivity`)

* **Controller:**
  Business logic and API handling
  (`WeatherController`, `LocationController`)

---

## 🛠️ Tech Stack

* Java (Android)
* Android SDK (API 26+)
* Retrofit (API communication)
* OkHttp (network layer)
* Gson (JSON parsing)
* OpenWeatherMap API
* OpenStreetMap (osmdroid)
* Google Play Services (Location)
* JUnit & Mockito (Testing)

---


## ⚙️ Setup & Installation

### 1. Clone the repository

git clone https://github.com/Smoketornado/WeatherApp.git
cd WeatherApp

### 2. Open in Android Studio

* Open project
* Sync Gradle

### 3. Add your API key

Go to:
WeatherController.java

Replace:
private static final String API_KEY = "YOUR_API_KEY_HERE";

with your OpenWeatherMap API key.

👉 Get one here: https://openweathermap.org/api

### 4. Run the app

* Start emulator or connect device
* Run project

---

## 🔐 Permissions

* INTERNET
* ACCESS_NETWORK_STATE
* ACCESS_FINE_LOCATION
* ACCESS_COARSE_LOCATION

---

## 🧪 Testing

Run tests:

./gradlew test

---

## 📚 What I learned

* Building Android apps using MVC architecture
* Working with REST APIs (Retrofit)
* Handling location services and permissions
* Integrating maps (OpenStreetMap)
* Writing unit tests with JUnit and Mockito

---

## 🚀 Future Improvements

* Add weather forecast (multi-day)
* Improve UI/UX (Material Design 3)
* Add dark mode
* Use MVVM instead of MVC
* Store last location locally

---

## 📄 License

This project is licensed under the MIT License.

---

## 🙌 Acknowledgments

* OpenWeatherMap API
* OpenStreetMap (osmdroid)
* Retrofit & OkHttp
