# Location Mock Android App

A modern Android application for mocking GPS location on Android devices. This app allows you to spoof your device's location for testing, development, or privacy purposes.

## Features

- **Modern Material Design UI**: Clean and intuitive interface
- **Real-time Location Mocking**: Continuously provides mock location updates
- **Custom Coordinates**: Enter any latitude and longitude coordinates
- **Foreground Service**: Runs in the background with persistent notification
- **Permission Handling**: Proper handling of location and mock location permissions
- **GPS Provider Support**: Uses Android's GPS provider for location mocking

## Prerequisites

Before using this app, you need to:

1. **Enable Developer Options** on your Android device:
   - Go to Settings > About Phone
   - Tap "Build Number" 7 times to enable Developer Options

2. **Enable Mock Location**:
   - Go to Settings > Developer Options
   - Find "Allow mock locations" and enable it
   - Make sure this app is selected as the mock location app

3. **Enable GPS**:
   - Go to Settings > Location
   - Enable Location services
   - Enable GPS

## Installation

1. Clone or download this project
2. Open the project in Android Studio
3. Connect your Android device or start an emulator
4. Build and run the application

## Usage

1. **Launch the App**: Open the Location Mock app on your device

2. **Grant Permissions**: The app will request location permissions - grant them

3. **Enter Coordinates**: 
   - Enter the desired latitude (between -90 and 90)
   - Enter the desired longitude (between -180 and 180)
   - Default coordinates are set to New York City (40.7128, -74.0060)

4. **Start Mocking**: Tap the "Start Mocking" button to begin location spoofing

5. **Stop Mocking**: Tap the "Stop Mocking" button to stop location spoofing

## How It Works

The app uses Android's `LocationManager` with test providers to inject mock location data into the system. Here's the technical flow:

1. **Permission Check**: Verifies location and mock location permissions
2. **Service Creation**: Creates a foreground service for persistent operation
3. **Test Provider**: Adds a GPS test provider to the LocationManager
4. **Location Injection**: Continuously provides mock location updates
5. **System Integration**: Other apps receive the mock location as if it were real GPS data

## Technical Details

### Architecture
- **MainActivity**: Handles UI interactions and permission requests
- **LocationMockService**: Foreground service that provides mock location updates
- **Material Design**: Modern UI components for better user experience

### Key Components

#### MainActivity.kt
- Permission handling with `ActivityResultContracts`
- Input validation for coordinates
- Service lifecycle management
- UI state management

#### LocationMockService.kt
- Foreground service implementation
- Test provider management
- Periodic location updates
- Notification management

### Permissions Required
- `ACCESS_FINE_LOCATION`: For precise location access
- `ACCESS_COARSE_LOCATION`: For approximate location access
- `ACCESS_MOCK_LOCATION`: For mock location functionality
- `FOREGROUND_SERVICE`: For running background service
- `FOREGROUND_SERVICE_LOCATION`: For location-specific foreground service

## Troubleshooting

### Common Issues

1. **"Mock location not working"**
   - Ensure Developer Options are enabled
   - Verify "Allow mock locations" is enabled
   - Make sure this app is selected as the mock location app

2. **"Location permissions required"**
   - Grant location permissions when prompted
   - Go to Settings > Apps > Location Mock > Permissions if needed

3. **"GPS not enabled"**
   - Enable GPS in device settings
   - The app will prompt you to enable GPS if needed

4. **"Service not starting"**
   - Check if the app has necessary permissions
   - Ensure the device supports mock location functionality

### Device Compatibility

This app requires:
- Android API level 24 (Android 7.0) or higher
- Device with GPS capabilities
- Developer Options enabled
- Mock location support (most devices have this)

## Security Considerations

⚠️ **Important**: This app is intended for development and testing purposes only. Using mock location in production apps or for malicious purposes may violate terms of service of other applications.

## Development

### Building from Source

1. Ensure you have Android Studio installed
2. Clone the repository
3. Open the project in Android Studio
4. Sync Gradle files
5. Build the project

### Project Structure
```
app/
├── src/main/
│   ├── java/in/tutorials/mocklocation/
│   │   ├── MainActivity.kt
│   │   └── LocationMockService.kt
│   ├── res/
│   │   ├── layout/activity_main.xml
│   │   └── values/strings.xml
│   └── AndroidManifest.xml
└── build.gradle.kts
```

## License

This project is provided as-is for educational and development purposes.

## Contributing

Feel free to submit issues and enhancement requests!

---

**Note**: This app requires root access or Developer Options to function properly. Mock location functionality is a development feature and may not work on all devices or Android versions. 