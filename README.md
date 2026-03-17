# Weather App

A complete Android weather application built with Java following the MVC (Model-View-Controller) architecture pattern. The app displays current weather information using the OpenWeatherMap API and shows the user's location on an interactive map using OpenStreetMap.

## Features

- **Current Weather Display**: Shows temperature, min/max temperature, weather condition, humidity, pressure, and wind speed
- **Location Services**: Uses FusedLocationProviderClient to get the user's current location
- **Interactive Map**: Displays user location on OpenStreetMap using osmdroid
- **Three Screens**: Main screen, Details screen, and Map screen
- **MVC Architecture**: Clean separation of concerns with Model, View, and Controller layers
- **Unit Tests**: Comprehensive JUnit tests for controllers and models

## Project Structure

```
WeatherApp/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/weatherapp/
│   │   │   │   ├── model/
│   │   │   │   │   └── WeatherData.java              # Data models
│   │   │   │   ├── controller/
│   │   │   │   │   ├── WeatherController.java        # Weather business logic
│   │   │   │   │   └── LocationController.java       # Location services
│   │   │   │   ├── api/
│   │   │   │   │   ├── WeatherApiService.java        # Retrofit API interface
│   │   │   │   │   └── ApiClient.java                # Retrofit configuration
│   │   │   │   ├── MainActivity.java                 # Main screen
│   │   │   │   ├── DetailsActivity.java              # Details screen
│   │   │   │   └── MapActivity.java                  # Map screen
│   │   │   ├── res/
│   │   │   │   ├── layout/
│   │   │   │   │   ├── activity_main.xml            # Main screen layout
│   │   │   │   │   ├── activity_details.xml         # Details screen layout
│   │   │   │   │   └── activity_map.xml             # Map screen layout
│   │   │   │   ├── values/
│   │   │   │   │   └── colors.xml                   # Color resources
│   │   │   │   └── drawable/
│   │   │   │       ├── ic_refresh.xml               # Refresh icon
│   │   │   │       └── ic_my_location.xml           # Location icon
│   │   │   └── AndroidManifest.xml                  # App permissions and activities
│   │   └── test/
│   │       └── java/com/example/weatherapp/
│   │           ├── controller/
│   │           │   ├── WeatherControllerTest.java    # Weather controller tests
│   │           │   └── LocationControllerTest.java   # Location controller tests
│   │           └── model/
│   │               └── WeatherDataTest.java          # Model tests
│   └── build.gradle.kts                             # App dependencies
├── gradle/
│   └── libs.versions.toml                           # Dependency versions
└── README.md                                        # This file
```

## Architecture Overview

### MVC Pattern Implementation

**Model (Data Layer):**
- `WeatherData.java`: Represents weather information from OpenWeatherMap API
- Contains nested classes for different weather components (MainWeather, WeatherCondition, Wind, Coordinates)

**View (UI Layer):**
- `MainActivity.java`: Main screen displaying current weather
- `DetailsActivity.java`: Detailed weather information screen
- `MapActivity.java`: Interactive map screen
- XML layouts for each activity

**Controller (Business Logic Layer):**
- `WeatherController.java`: Handles weather API calls and data formatting
- `LocationController.java`: Manages location services and permissions
- `WeatherApiService.java`: Retrofit interface for API calls
- `ApiClient.java`: Retrofit configuration and setup

## Setup Instructions

### Prerequisites

- Android Studio Arctic Fox or later
- Android SDK API level 26 or higher
- OpenWeatherMap API key (free tier)

### Installation

1. **Clone the repository:**
   ```bash
   git clone <repository-url>
   cd WeatherApp
   ```

2. **Get OpenWeatherMap API Key:**
   - Visit [OpenWeatherMap](https://openweathermap.org/api)
   - Sign up for a free account
   - Get your API key

3. **Configure API Key:**
   - Open `app/src/main/java/com/example/weatherapp/controller/WeatherController.java`
   - Replace `YOUR_API_KEY_HERE` with your actual API key:
   ```java
   private static final String API_KEY = "your_actual_api_key_here";
   ```

4. **Build and Run:**
   - Open the project in Android Studio
   - Sync Gradle files
   - Build the project
   - Run on an emulator or physical device

### Permissions

The app requires the following permissions:
- `INTERNET`: For API calls
- `ACCESS_NETWORK_STATE`: For network connectivity checks
- `ACCESS_FINE_LOCATION`: For precise location
- `ACCESS_COARSE_LOCATION`: For approximate location
- `WRITE_EXTERNAL_STORAGE`: For osmdroid cache (Android < 29)

## API Usage

### OpenWeatherMap API (Free Tier)

The app uses only the free tier features:
- Current weather data
- Temperature (current, min, max)
- Humidity and pressure
- Wind speed
- Weather description
- Location name

**Not included (paid features):**
- UV index
- Air quality
- Weather forecasts
- One Call API 3.0

### API Endpoints Used

- `GET /weather`: Current weather by coordinates or city name
- Parameters: `lat`, `lon`, `appid`, `units`

## Testing

### Unit Tests

Run unit tests using:
```bash
./gradlew test
```

**Test Coverage:**
- `WeatherControllerTest.java`: Tests formatting methods and API calls
- `LocationControllerTest.java`: Tests permission handling and location services
- `WeatherDataTest.java`: Tests model structure and getters

### Instrumentation Tests

Run instrumentation tests using:
```bash
./gradlew connectedAndroidTest
```

## Dependencies

### Core Dependencies
- **Retrofit 2.11.0**: HTTP client for API calls
- **OkHttp 4.12.0**: HTTP client library
- **Gson**: JSON parsing
- **osmdroid 6.1.18**: OpenStreetMap integration
- **Google Play Services Location**: Location services

### UI Dependencies
- **Material Design Components**: Modern UI components
- **ConstraintLayout**: Flexible layouts
- **CardView**: Card-based UI design

### Testing Dependencies
- **JUnit 4.13.2**: Unit testing framework
- **Mockito 5.8.0**: Mocking framework
- **Espresso**: UI testing

## Usage

### Main Screen
- Displays current weather information
- Shows location name and last updated time
- Refresh button to update weather data
- Navigation buttons to Details and Map screens

### Details Screen
- Expanded view of all weather information
- Organized in sections (Temperature, Weather Conditions, Atmospheric Conditions, Coordinates)
- Back button to return to main screen

### Map Screen
- Interactive OpenStreetMap showing user location
- Location marker with coordinates
- Center location button
- Back button to return to main screen

## Troubleshooting

### Common Issues

1. **API Key Error:**
   - Ensure you've replaced the placeholder API key
   - Verify your API key is valid and active
   - Check API usage limits

2. **Location Permission:**
   - Grant location permissions when prompted
   - Enable location services on device
   - Check app permissions in device settings

3. **Network Issues:**
   - Ensure internet connectivity
   - Check firewall settings
   - Verify API endpoint accessibility

4. **Map Not Loading:**
   - Check internet connection
   - Verify osmdroid configuration
   - Clear app cache if needed

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests for new functionality
5. Submit a pull request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Acknowledgments

- [OpenWeatherMap](https://openweathermap.org/) for weather data API
- [osmdroid](https://github.com/osmdroid/osmdroid) for OpenStreetMap integration
- [Retrofit](https://square.github.io/retrofit/) for HTTP client functionality
- [Material Design](https://material.io/) for UI components 