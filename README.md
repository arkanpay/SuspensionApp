# Suspension Analyzer - Real-Time Vehicle Suspension Testing App

A professional Android application for testing and analyzing vehicle suspension performance using phone sensors with real-time 3D visualization.

## Features

### Core Functionality
- **Real-Time 3D Car Visualization** - 3D wireframe car model that moves with phone orientation
- **Live G-Force Monitoring** - Track lateral, longitudinal, and vertical G-forces
- **Peak G-Force Tracking** - Records maximum G-forces during test runs
- **Sensor Calibration** - Calibrate accelerometer and gyroscope for accurate readings
- **Database Storage** - SQLite database for vehicle profiles and test runs
- **Backend API Integration** - Upload test data to server for benchmarking

### What's Working Now
✅ Real-time sensor data collection (accelerometer, gyroscope, magnetometer, barometer)
✅ Kalman filtering for smooth orientation tracking
✅ 3D car model with pitch, roll, yaw visualization
✅ Live G-force display with color coding
✅ Peak G-force tracking during tests
✅ Sensor calibration system
✅ Clean architecture with Room database

## Project Structure

```
EvansApp/
├── app/
│   ├── build.gradle.kts              # App dependencies and config
│   └── src/main/
│       ├── java/com/suspension/analyzer/
│       │   ├── MainActivity.kt        # Main UI controller
│       │   ├── sensors/
│       │   │   └── SensorDataManager.kt    # Sensor fusion & data collection
│       │   ├── data/
│       │   │   └── Database.kt        # Room database schema
│       │   ├── test/
│       │   │   └── TestProtocolManager.kt  # Test protocol validation
│       │   ├── network/
│       │   │   └── NetworkClient.kt   # Backend API client
│       │   └── ui/
│       │       └── CarVisualization.kt     # 3D car renderer
│       ├── res/
│       │   ├── layout/
│       │   │   └── activity_main.xml  # Main UI layout
│       │   └── values/
│       │       ├── strings.xml
│       │       ├── colors.xml
│       │       └── themes.xml
│       └── AndroidManifest.xml
├── backend_api.py                     # Flask backend API
├── build.gradle.kts                   # Project-level build config
├── settings.gradle.kts                # Gradle settings
└── gradle.properties                  # Gradle properties
```

## Requirements

### Hardware
- Android device with:
  - Accelerometer (required)
  - Gyroscope (required)
  - Magnetometer (optional)
  - Barometer (optional)
  - Camera (for road surface photos)
  - GPS (for location data)

### Software
- Android Studio Hedgehog (2023.1.1) or newer
- Android SDK 26+ (minimum)
- Android SDK 34 (target)
- Kotlin 1.9.0+
- Gradle 8.1.0+

## Installation

### 1. Clone/Open Project
```bash
cd /Users/chadsteele/Desktop/EvansApp
```

### 2. Open in Android Studio
- File → Open → Select `EvansApp` folder
- Android Studio will automatically sync Gradle dependencies

### 3. Build the Project
```bash
./gradlew build
```

### 4. Run on Device
- Connect Android device via USB (with Developer Mode enabled)
- Click "Run" in Android Studio
- OR use command line:
```bash
./gradlew installDebug
```

## Usage

### First Time Setup
1. Launch the app
2. Grant sensor and location permissions when prompted
3. **IMPORTANT:** Calibrate sensors before first use:
   - Place phone on flat, stable surface
   - Click "Calibrate" button
   - Wait for calibration to complete

### Running a Test
1. Mount phone securely in vehicle (dashboard or windshield mount recommended)
2. Click "Start Test" button
3. Drive the test scenario (acceleration, cornering, etc.)
4. Observe real-time 3D car orientation and G-forces
5. Click "Stop Test" when complete
6. View peak G-force results

### Understanding the Display

**3D Car Model**
- Shows real-time orientation of your phone (and vehicle)
- Pitch: Forward/backward tilt
- Roll: Left/right tilt
- Yaw: Rotation around vertical axis

**G-Force Readings**
- **Lateral G**: Side-to-side forces (cornering)
- **Longitudinal G**: Forward/backward forces (acceleration/braking)
- **Vertical G**: Up/down forces (bumps, jumps)
- **Total G**: Combined magnitude

**Color Coding**
- Green: < 1.0 G (normal driving)
- Yellow: 1.0 - 1.5 G (spirited driving)
- Red: > 1.5 G (aggressive driving)

## Backend Setup (Optional)

The app can upload test data to a backend server for benchmarking.

### Running the Backend
```bash
cd /Users/chadsteele/Desktop/EvansApp
pip install -r requirements.txt
python backend_api.py
```

Backend will run on `http://localhost:5000`

### Backend Endpoints
- `POST /api/v1/vehicles` - Register vehicle
- `POST /api/v1/test-runs/submit` - Submit test data
- `GET /api/v1/benchmarks/compare` - Get benchmark comparison
- `GET /api/v1/health` - Health check

## Technical Details

### Sensor Fusion
The app uses a Kalman filter to fuse accelerometer and gyroscope data for accurate orientation tracking:
- Process noise: 0.02
- Measurement noise: 0.01
- Update rate: ~100Hz

### Coordinate System
- **X-axis**: Device width (left/right)
- **Y-axis**: Device height (up/down)
- **Z-axis**: Device depth (forward/back)

### G-Force Calculations
```kotlin
lateralG = sqrt(accelX² + accelZ²) / 9.81
longitudinalG = accelY / 9.81
verticalG = accelZ / 9.81
totalG = sqrt(accelX² + accelY² + accelZ²) / 9.81
```

## Troubleshooting

### Sensors Not Working
- Ensure device has required sensors (check Settings → About Phone → Sensors)
- Grant all permissions (Location, Camera)
- Restart app

### Drift in Orientation
- Recalibrate sensors (phone must be completely still)
- Check for magnetic interference (remove from metal surfaces)

### Inaccurate G-Force Readings
- Calibrate before each session
- Ensure phone is rigidly mounted (no vibration)
- Avoid touching phone during test

## Future Enhancements

Planned features for future versions:
- OBD-II integration for speed/RPM data
- Multiple standardized test protocols
- Photo capture of road conditions
- GPS speed tracking
- Test protocol compliance scoring
- Vehicle profile management UI
- Export test data to CSV/JSON
- Cloud sync and crowdsourced benchmarking
- Advanced analytics and graphs

## Architecture

### Design Patterns
- **MVVM**: Separation of UI and business logic
- **Repository Pattern**: Clean data access layer
- **Singleton**: Database instance management
- **Observer Pattern**: Sensor data callbacks

### Key Components
- **SensorDataManager**: Hardware sensor interface with Kalman filtering
- **CarVisualization**: Custom View with 3D projection math
- **Database**: Room ORM for local persistence
- **NetworkClient**: Retrofit-based API client

## License

Proprietary - Evans Suspension Testing Application

## Credits

Developed for automotive suspension analysis and benchmarking.

---

## Quick Start Checklist

- [ ] Open project in Android Studio
- [ ] Sync Gradle dependencies
- [ ] Connect Android device
- [ ] Build and install app
- [ ] Grant permissions
- [ ] Calibrate sensors
- [ ] Mount phone in vehicle
- [ ] Run first test!

For issues or questions, check the troubleshooting section above.
