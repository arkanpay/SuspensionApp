# Quick Start Guide - Suspension Analyzer

## Build and Install (5 minutes)

### Prerequisites
- Android Studio installed
- Android device with USB debugging enabled
- USB cable

### Steps

1. **Open Project**
   ```bash
   # Open Android Studio
   # File → Open → /Users/chadsteele/Desktop/EvansApp
   ```

2. **Wait for Sync**
   - Android Studio will automatically sync Gradle dependencies
   - Wait for "BUILD SUCCESSFUL" in bottom status bar

3. **Connect Device**
   - Enable Developer Options on your Android phone:
     - Settings → About Phone → Tap "Build Number" 7 times
   - Enable USB Debugging:
     - Settings → Developer Options → USB Debugging
   - Connect phone via USB
   - Accept "Allow USB debugging" prompt on phone

4. **Run App**
   - Click green "Run" button in Android Studio toolbar
   - Select your device from list
   - App will build and install automatically

## First Use

### 1. Grant Permissions
When app launches, grant these permissions:
- ✅ Location (for GPS data)
- ✅ Camera (for road photos)

### 2. Calibrate Sensors
**CRITICAL STEP - DO NOT SKIP!**
- Place phone on flat, stable surface (table or desk)
- Phone must be completely still
- Click "CALIBRATE" button
- Wait 2-3 seconds for completion
- You'll see "Calibration Complete" message

### 3. Test at Desk First
Before going to car:
- Pick up phone and tilt it
- Watch 3D car model move in real-time
- Shake phone gently - see G-forces spike
- Rotate phone - watch orientation change

## In-Vehicle Testing

### 1. Mount Phone
- Use dashboard mount or windshield mount
- Phone must be rigidly mounted (no wobble)
- Portrait orientation recommended
- Ensure screen stays on

### 2. Calibrate in Car
- With phone mounted and car parked on level ground
- Click "CALIBRATE" again
- This zeros out the mounting angle

### 3. Start Test
- Click "START TEST" button
- Button turns red, "Recording..." appears
- Drive normally or perform test maneuver

### 4. Observe Data
**3D Car Model**
- Tilts with vehicle orientation
- Front of car points toward windshield

**G-Force Display**
- **Green** = Normal driving (< 1.0 G)
- **Yellow** = Spirited driving (1.0-1.5 G)
- **Red** = Aggressive driving (> 1.5 G)

**Peak Values**
- Shows maximum G-forces recorded during test
- Updates continuously while recording

### 5. Stop Test
- Click "STOP TEST" button
- View peak G-force summary in popup
- Ready for next test

## Understanding the Display

### Main Display Components

```
┌─────────────────────────────────┐
│ STATUS: Recording...            │ ← Current state
├─────────────────────────────────┤
│                                 │
│      [3D Car Visualization]     │ ← Moves with phone
│                                 │
├─────────────────────────────────┤
│  Total G-Force: 1.23 G          │ ← Combined magnitude
├─────────────────────────────────┤
│ Lateral     Long.     Vertical  │
│  0.45 G     0.82 G     0.15 G   │ ← Individual axes
├─────────────────────────────────┤
│ Peak: L:0.89 | Lo:1.15 | V:0.34 │ ← Maximum values
├─────────────────────────────────┤
│ [CALIBRATE]     [START TEST]    │ ← Control buttons
└─────────────────────────────────┘
```

### G-Force Meanings

**Lateral (Side-to-Side)**
- Left/right cornering forces
- Typical: 0.3-0.7 G in normal turns
- High: > 1.0 G in aggressive cornering

**Longitudinal (Forward/Back)**
- Acceleration and braking forces
- Typical: 0.2-0.5 G normal acceleration
- High: > 0.8 G hard braking

**Vertical (Up/Down)**
- Bumps, dips, suspension compression
- Typical: 0.1-0.3 G normal road
- High: > 0.5 G hitting pothole

## Troubleshooting

### App Won't Install
- Check Android version (need API 26+, Android 8.0+)
- Enable "Install from Unknown Sources" if needed
- Try: Settings → Apps → Special Access → Install Unknown Apps

### 3D Car Doesn't Move
- Calibrate sensors first
- Check device has gyroscope: Settings → About Phone
- Restart app
- Try moving phone more dramatically

### G-Force Values Seem Wrong
- **Recalibrate!** (This fixes 90% of issues)
- Ensure phone mount is rigid
- Check for magnetic interference (remove from metal)
- Let phone "settle" for 5 seconds after calibration

### Orientation Drifts
- Normal for gyroscope over time
- Recalibrate every 5-10 minutes
- Future update will add magnetometer correction

### Permission Errors
- Grant all requested permissions
- Check: Settings → Apps → Suspension Analyzer → Permissions
- Enable Location and Camera manually if needed

## Test Scenarios

### Basic Acceleration Test
1. Calibrate while parked
2. Start test
3. Accelerate smoothly to 40 mph
4. Coast and brake smoothly
5. Stop test
6. Review peak longitudinal G

### Cornering Test
1. Find empty parking lot or safe road
2. Start test
3. Drive in figure-8 pattern
4. Vary speed and tightness
5. Stop test
6. Review peak lateral G

### Suspension Bump Test
1. Find road with speed bump or dip
2. Start test
3. Drive over bump at various speeds
4. Stop test
5. Compare vertical G at different speeds

## Tips for Best Results

✅ **Always calibrate** before each test session
✅ **Rigid mount** is critical - no wobble
✅ **Let it settle** for 2-3 seconds after calibration
✅ **Safe driving** - don't focus on screen while moving
✅ **Consistent conditions** - compare same road/speed
✅ **Battery** - keep phone charged (sensors drain battery)

## What to Expect

### Normal Values
- **City driving**: 0.2-0.5 G total
- **Highway**: 0.1-0.3 G total
- **Spirited driving**: 0.7-1.2 G
- **Track/autocross**: 1.0-1.5 G

### Phone Orientation
- Portrait is standard
- Landscape will work but UI may look odd
- Keep consistent for comparable tests

### Battery Usage
- Moderate drain (sensors + screen)
- Expect 2-3 hours continuous use
- Use car charger for long sessions

## Need Help?

Check these files in the project:
- `README.md` - Full documentation
- `PROJECT_SUMMARY.md` - What was built
- `backend_api.py` - Backend setup (optional)

Common fixes:
1. **Recalibrate** (fixes 90% of issues)
2. **Restart app**
3. **Check permissions**
4. **Ensure device has gyroscope**

---

## That's It!

You now have a working suspension testing app with:
- ✅ Real-time 3D visualization
- ✅ Live G-force monitoring
- ✅ Peak tracking
- ✅ Professional data collection

**Ready to test!** 🚗💨
