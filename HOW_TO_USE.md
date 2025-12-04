# How to Use - Suspension Analyzer

## Quick Start (5 Minutes)

### 1. Build and Install
```bash
# Open in Android Studio
File → Open → /Users/chadsteele/Desktop/EvansApp

# Wait for Gradle sync
# Connect Android device
# Click Run button
```

### 2. First Launch
- Grant Location and Camera permissions
- Click "Calibrate" button (phone on flat surface)
- Wait for "Calibration Complete"

### 3. Run Your First Test
- Click "Start Test"
- Choose "Standing Start/Stop"
- Click "Start Test" on the card
- Drive as instructed (accel to 40mph, brake to stop)
- View your score!

## Complete Walkthrough

### Main Screen

When you launch the app, you'll see:
```
┌─────────────────────────────────┐
│ Status: Sensors Active          │
├─────────────────────────────────┤
│    [3D Car Visualization]       │
│    (Tilts with phone)           │
├─────────────────────────────────┤
│ Total G-Force: 0.00 G           │
│ Lateral | Longitudinal | Vert   │
├─────────────────────────────────┤
│ [CALIBRATE]    [START TEST]     │
└─────────────────────────────────┘
```

**What to do:**
1. Test sensors by moving phone → 3D car should move
2. Click **CALIBRATE** if phone hasn't been calibrated yet
3. Click **START TEST** when ready

---

### Test Selection Screen

After clicking "Start Test":
```
┌─────────────────────────────────┐
│   Select Test Protocol          │
├─────────────────────────────────┤
│                                 │
│  ┌───────────────────────────┐ │
│  │ Standing Start/Stop       │ │
│  │ Tests accel and braking   │ │
│  │ Duration: ~30 seconds     │ │
│  │ [START TEST]              │ │
│  └───────────────────────────┘ │
│                                 │
│  ┌───────────────────────────┐ │
│  │ Figure-8                  │ │
│  │ Tests cornering control   │ │
│  │ Duration: ~45 seconds     │ │
│  │ [START TEST]              │ │
│  └───────────────────────────┘ │
│                                 │
└─────────────────────────────────┘
```

**What to do:**
- Read test instructions
- Choose the test you want to run
- Click **START TEST** on your chosen test

---

### Test Execution Screen

During the test:
```
┌─────────────────────────────────┐
│ Standing Start/Stop - 25s       │ ← Countdown
├─────────────────────────────────┤
│    [3D Car Moving in Real-Time] │
├─────────────────────────────────┤
│ Total G-Force: 0.85 G (YELLOW)  │
│ Lateral: 0.12 G                 │
│ Longitudinal: 0.82 G ← Active   │
│ Vertical: 0.15 G                │
├─────────────────────────────────┤
│         [STOP TEST]             │
└─────────────────────────────────┘
```

**What to do:**
- Drive according to test instructions
- Watch G-forces update in real-time
- See 3D car orientation
- Test automatically completes when timer finishes
- Or click **STOP TEST** to end early

---

### Results Screen

After test completes:
```
┌─────────────────────────────────┐
│        Test Results             │
│   Standing Start/Stop Test      │
├─────────────────────────────────┤
│                                 │
│       Overall Score             │
│           85                    │ ← Big number
│          Good                   │ ← Rating
│                                 │
├─────────────────────────────────┤
│  Performance Breakdown          │
├─────────────────────────────────┤
│  Acceleration        90         │
│  Braking             85         │
│  Smoothness          75         │
├─────────────────────────────────┤
│  Test Metrics                   │
├─────────────────────────────────┤
│  Peak Acceleration   0.85 G     │
│  Peak Braking        0.92 G     │
│  Vertical Stability  0.18 G var │
├─────────────────────────────────┤
│  [SHARE]        [NEW TEST]      │
└─────────────────────────────────┘
```

**What to do:**
- Review your overall score (0-100)
- Check category scores
- Read detailed metrics
- Click **SHARE** to send report to someone
- Click **NEW TEST** to run another test

---

## Test Instructions

### Standing Start/Stop Test (30 seconds)

**Setup:**
1. Find straight road with light traffic
2. Mount phone securely in vehicle
3. Calibrate sensors
4. Start test

**Driving:**
1. Begin from complete stop
2. Accelerate **smoothly** to 40 mph (takes ~8-10 seconds)
3. Coast briefly (2-3 seconds)
4. Brake **smoothly** to complete stop (takes ~8-10 seconds)
5. Done!

**What it measures:**
- **Acceleration:** How quickly you can accelerate
- **Braking:** How effectively you can brake
- **Smoothness:** How stable suspension is during acceleration/braking

**Tips for good scores:**
- Smooth inputs (don't jerk steering/pedals)
- Straight line (avoid turning during test)
- Consistent surface (avoid bumps if possible)
- Proper tire pressure

---

### Figure-8 Test (45 seconds)

**Setup:**
1. Find empty parking lot (100x100 ft minimum)
2. Mount phone securely in vehicle
3. Calibrate sensors
4. Start test

**Driving:**
1. Drive continuous figure-8 pattern
2. Maintain **consistent** 20-25 mph
3. Make smooth, connected turns
4. Complete at least 2 full figure-8s
5. Done!

**What it measures:**
- **Cornering:** How much lateral G-force (grip) you can generate
- **Consistency:** How smoothly you maintain G-forces
- **Body Roll:** How well suspension controls body motion

**Tips for good scores:**
- Consistent speed throughout
- Smooth steering inputs
- Circular turns (not sharp angles)
- Equal left and right turns

---

## Understanding Your Scores

### Overall Score (0-100)
```
90-100  Excellent  🟢  High-performance suspension
75-89   Good       🟢  Above average performance
60-74   Average    🟡  Typical stock performance
40-59   Fair       🟠  Below average
0-39    Poor       🔴  Needs attention
```

### Category Scores

**Acceleration (Standing Start/Stop only)**
- Based on peak longitudinal G during acceleration
- High score = strong acceleration capability
- Affected by: Engine power, tire grip, weight

**Braking (Standing Start/Stop only)**
- Based on peak longitudinal G during braking
- High score = strong braking capability
- Affected by: Brake condition, tire grip, ABS

**Cornering (Figure-8 only)**
- Based on peak lateral G during turns
- High score = high cornering capability
- Affected by: Tire grip, suspension stiffness, sway bars

**Smoothness**
- Based on vertical G-force variation
- High score = suspension controls body motion well
- Affected by: Shock condition, spring rates, bushings

### Example Scores by Vehicle Type

**Family Sedan (Stock)**
- Standing Start/Stop: 60-70
- Figure-8: 55-65
- Rating: Average

**Sports Car (Stock)**
- Standing Start/Stop: 75-85
- Figure-8: 75-85
- Rating: Good

**Modified Sports Car**
- Standing Start/Stop: 85-95
- Figure-8: 85-95
- Rating: Excellent

**Truck/SUV**
- Standing Start/Stop: 50-65
- Figure-8: 40-55
- Rating: Fair-Average

---

## Sharing Your Results

### Share Button

When you click **SHARE**, the app creates a text report:

```
SUSPENSION TEST RESULTS
========================================

Test: Standing Start/Stop
Date: 2024-12-03 15:30

OVERALL SCORE: 85/100
Rating: Good

PERFORMANCE BREAKDOWN
----------------------------------------
Acceleration:  90/100
Braking:       85/100
Smoothness:    75/100

TEST METRICS
----------------------------------------
Peak Acceleration: 0.85 G
Peak Braking: 0.92 G
Vertical Stability: 0.18 G variation

Generated by Suspension Analyzer App
```

**Where to share:**
- Messages (text to friend)
- Email (send to mechanic)
- Notes app (save for later)
- Social media (show off your score!)

---

## Tips for Best Results

### Before Testing

✅ **Calibrate first** - Always calibrate sensors before each test
✅ **Secure mount** - Phone must not move during test
✅ **Check tires** - Proper pressure for accurate results
✅ **Consistent conditions** - Same location, similar weather
✅ **Battery charged** - Sensors drain battery

### During Testing

✅ **Follow instructions** - Each test has specific requirements
✅ **Smooth inputs** - Jerky movements hurt scores
✅ **Consistent speed** - Especially for Figure-8
✅ **Safe driving** - Don't exceed your skill level
✅ **Focus on driving** - Don't watch screen during test

### After Testing

✅ **Review metrics** - Understand what affected your score
✅ **Compare tests** - Run same test multiple times
✅ **Track changes** - Test before/after modifications
✅ **Share results** - Get feedback from others

---

## Troubleshooting

### Low Scores (Lower than expected)

**Possible causes:**
- Incorrect calibration → Recalibrate
- Phone not mounted rigidly → Improve mount
- Low tire pressure → Check and adjust
- Worn suspension → May need service
- Didn't follow test protocol → Read instructions

### 3D Car Not Moving

- Sensors not initialized → Restart app
- Device missing gyroscope → Check phone specs
- Calibration issue → Recalibrate

### Timer Doesn't Start

- Permissions not granted → Check settings
- App crashed → Restart app

### Can't Share Results

- No apps to share with → Install messaging app
- File access denied → Grant storage permission

---

## Common Questions

**Q: How often should I test?**
A: Monthly for tracking, or before/after any suspension changes.

**Q: Can I test multiple cars?**
A: Yes! Just run the same test on different vehicles and compare scores.

**Q: Do I need internet?**
A: No, app works completely offline.

**Q: How accurate are the scores?**
A: Very accurate for relative comparisons. Absolute values depend on proper calibration and execution.

**Q: What's a "good" score?**
A: 75+ is good for most vehicles. 85+ is excellent. Compare to your baseline, not others.

**Q: Can I compare my score to others?**
A: Scores are most useful for comparing YOUR car over time, or different cars on the same test.

---

## Next Steps

1. ✅ Build and install app
2. ✅ Calibrate sensors
3. ✅ Run Standing Start/Stop test
4. ✅ View your first score!
5. ✅ Run Figure-8 test
6. ✅ Compare the two scores
7. ✅ Share results with friend/mechanic

**Ready to test your suspension!** 🚗💨
