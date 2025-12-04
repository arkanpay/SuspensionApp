{\rtf1\ansi\ansicpg1252\cocoartf2865
\cocoatextscaling0\cocoaplatform0{\fonttbl\f0\froman\fcharset0 Times-Bold;\f1\froman\fcharset0 Times-Roman;}
{\colortbl;\red255\green255\blue255;\red0\green0\blue0;}
{\*\expandedcolortbl;;\cssrgb\c0\c0\c0;}
{\*\listtable{\list\listtemplateid1\listhybrid{\listlevel\levelnfc0\levelnfcn0\leveljc0\leveljcn0\levelfollow0\levelstartat1\levelspace360\levelindent0{\*\levelmarker \{decimal\}}{\leveltext\leveltemplateid1\'01\'00;}{\levelnumbers\'01;}\fi-360\li720\lin720 }{\listname ;}\listid1}
{\list\listtemplateid2\listhybrid{\listlevel\levelnfc0\levelnfcn0\leveljc0\leveljcn0\levelfollow0\levelstartat1\levelspace360\levelindent0{\*\levelmarker \{decimal\}}{\leveltext\leveltemplateid101\'01\'00;}{\levelnumbers\'01;}\fi-360\li720\lin720 }{\listname ;}\listid2}
{\list\listtemplateid3\listhybrid{\listlevel\levelnfc0\levelnfcn0\leveljc0\leveljcn0\levelfollow0\levelstartat1\levelspace360\levelindent0{\*\levelmarker \{decimal\}}{\leveltext\leveltemplateid201\'01\'00;}{\levelnumbers\'01;}\fi-360\li720\lin720 }{\listname ;}\listid3}}
{\*\listoverridetable{\listoverride\listid1\listoverridecount0\ls1}{\listoverride\listid2\listoverridecount0\ls2}{\listoverride\listid3\listoverridecount0\ls3}}
\margl1440\margr1440\vieww11520\viewh8400\viewkind0
\deftab720
\pard\pardeftab720\sa240\partightenfactor0

\f0\b\fs24 \cf0 \expnd0\expndtw0\kerning0
V1 Feature List:
\f1\b0 \
\pard\tx220\tx720\pardeftab720\li720\fi-720\partightenfactor0
\ls1\ilvl0
\f0\b \cf0 \kerning1\expnd0\expndtw0 {\listtext	1	}\expnd0\expndtw0\kerning0
Sensor data collection
\f1\b0  \'97 Real-time accelerometer (XYZ), gyroscope (XYZ), magnetometer (XYZ) at 100Hz, stored locally with timestamps\
\ls1\ilvl0
\f0\b \kerning1\expnd0\expndtw0 {\listtext	2	}\expnd0\expndtw0\kerning0
Vehicle profile creation
\f1\b0  \'97 Make, model, year, suspension type (dropdown), notes field\
\ls1\ilvl0
\f0\b \kerning1\expnd0\expndtw0 {\listtext	3	}\expnd0\expndtw0\kerning0
Single standardized test
\f1\b0  \'97 "Smooth acceleration 0-40mph" with onscreen target curve overlay; driver follows the curve; app validates compliance and rejects poor runs\
\ls1\ilvl0
\f0\b \kerning1\expnd0\expndtw0 {\listtext	4	}\expnd0\expndtw0\kerning0
Photo capture
\f1\b0  \'97 Road surface photo with EXIF metadata (timestamp, location if available)\
\ls1\ilvl0
\f0\b \kerning1\expnd0\expndtw0 {\listtext	5	}\expnd0\expndtw0\kerning0
Local SQLite logging
\f1\b0  \'97 Full sensor stream + test metadata persisted\
\ls1\ilvl0
\f0\b \kerning1\expnd0\expndtw0 {\listtext	6	}\expnd0\expndtw0\kerning0
3D car visualization
\f1\b0  \'97 Real-time orientation (pitch, roll, yaw) displayed as rotating car model\
\ls1\ilvl0
\f0\b \kerning1\expnd0\expndtw0 {\listtext	7	}\expnd0\expndtw0\kerning0
Live G-force readout
\f1\b0  \'97 Peak lateral, longitudinal, vertical during the test\
\ls1\ilvl0
\f0\b \kerning1\expnd0\expndtw0 {\listtext	8	}\expnd0\expndtw0\kerning0
Basic backend submission
\f1\b0  \'97 POST test run (sensor data JSON + photo) to self-hosted API; vehicle profile matches on backend\
\ls1\ilvl0
\f0\b \kerning1\expnd0\expndtw0 {\listtext	9	}\expnd0\expndtw0\kerning0
Simple comparison view
\f1\b0  \'97 "Your peak lateral G: 0.87 | Baseline: 0.82"\
\ls1\ilvl0
\f0\b \kerning1\expnd0\expndtw0 {\listtext	10	}\expnd0\expndtw0\kerning0
Settings/calibration
\f1\b0  \'97 Static calibration mode to zero sensors before recording; phone mount position selector\
\pard\tx220\tx720\pardeftab720\li720\fi-720\partightenfactor0
\cf0 \
\
\pard\pardeftab720\sa240\partightenfactor0
\cf0 project direction. \
\pard\tx220\tx720\pardeftab720\li720\fi-720\partightenfactor0
\ls2\ilvl0\cf0 \kerning1\expnd0\expndtw0 {\listtext	1	}\expnd0\expndtw0\kerning0
A suspension tuning tool for specific vehicles\
\ls2\ilvl0\kerning1\expnd0\expndtw0 {\listtext	2	}\expnd0\expndtw0\kerning0
Crowdsourced benchmarking data (like Speedtest.net for cars)\
\ls2\ilvl0\kerning1\expnd0\expndtw0 {\listtext	3	}\expnd0\expndtw0\kerning0
Phone-based sensor only for v1\
\ls2\ilvl0\kerning1\expnd0\expndtw0 {\listtext	4	}\expnd0\expndtw0\kerning0
Optional OBD integration as a professional feature later\
\pard\pardeftab720\sa240\partightenfactor0

\f0\b \cf0 The benchmarking architecture:
\f1\b0 \
You'd need a backend database structure that captures vehicle baseline profiles (make, model, year, suspension type, tire spec, ride height) and then aggregates anonymized test results. Users submit runs and see how their suspension performs relative to baseline, similar models, or the same vehicle modified by others. The power emerges when someone's tuning changes produce measurable differences they can validate against the community dataset.\

\f0\b Standardizing test conditions:
\f1\b0 \
This is your critical challenge. Road surface, temperature, speed, driving style all skew results wildly. You might need a "normalized test suite"\'97like a specific maneuver (lane change at 40mph, defined bump input, smooth acceleration curve) that produces comparable data. Alternatively, let users log everything contextually (road type, weather, driver confidence rating) and let the database filter for apples-to-apples comparisons.\

\f0\b Data quality gates:
\f1\b0 \
Phone mounting matters hugely. Gyro drift, accelerometer bias\'97you'll need onboarding that validates sensor calibration and mounting position before data gets accepted into the benchmark. Maybe require a static calibration phase before recording starts.\

\f0\b The first-version scope:
\f1\b0 \
Build the 3D visualization and real-time logging first. Get the sensor fusion solid. Then add basic backend storage and vehicle profile matching. Launch with manual data submission (no fancy UI sync initially), then iterate on the benchmarking comparison features once you have actual user data flowing in.\

\f0\b Suspension-specific metrics to track:
\f1\b0 \
Peak G-force (acceleration and lateral), rebound settling time (how quickly gyro stabilizes after a bump), body roll rate during cornering, and vibration frequency response. These paint a clear picture of suspension behavior.\
\pard\pardeftab720\sa240\partightenfactor0

\f0\b \cf0 \outl0\strokewidth0 \strokec2 Standardized test protocol framework:
\f1\b0 \strokec2 \
Build a tiered test suite. Start simple: accelerate smoothly from 0-40mph following a target curve, lane change at steady speed, gradual deceleration. Each test displays real-time feedback (speed graph vs target, acceleration curve overlay) so the driver can match the expected input profile. The app rejects runs that deviate significantly from the protocol\'97this is your quality gate. Save the accepted run with a "protocol compliance score."\

\f0\b \strokec2 Data schema for backend:
\f1\b0 \strokec2 \
Vehicle profile (make, model, year, suspension type, mods if any), test metadata (road surface photo with EXIF timestamp/location, ambient temp, humidity if available), test type, and then the full sensor stream (accelerometer XYZ, gyro XYZ, magnetometer, timestamps). Photo gets stored with hash and metadata separate from the binary\'97lets you do post-hoc analysis ("all smooth asphalt runs from this user" vs "all gravel").\

\f0\b \strokec2 Android architecture:
\f1\b0 \strokec2 \
Sensor fusion layer using SensorManager with a high-frequency listener (100Hz minimum for meaningful gyro data). Foreground Service during active testing to prevent the OS from throttling. Local SQLite buffers everything before backend sync\'97resilient if connection drops mid-test. Camera integration captures the road surface photo + EXIF, plus you can overlay a timestamp reference.\

\f0\b \strokec2 Backend\'97keep it straightforward:
\f1\b0 \strokec2 \
REST API endpoints for vehicle profile creation, test run submission (multipart for sensor JSON + photo), and query endpoints for comparisons. SQLite or PostgreSQL on your self-hosted instance. Comparison queries return percentile rankings (your car at 75th percentile lateral G vs baseline, 85th percentile rebound settling).\

\f0\b \strokec2 First-version milestones:
\f1\b0 \strokec2 \
\pard\tx220\tx720\pardeftab720\li720\fi-720\partightenfactor0
\ls3\ilvl0\cf0 \kerning1\expnd0\expndtw0 \outl0\strokewidth0 {\listtext	1	}\expnd0\expndtw0\kerning0
\outl0\strokewidth0 \strokec2 Sensor fusion and local logging working reliably\
\ls3\ilvl0\kerning1\expnd0\expndtw0 \outl0\strokewidth0 {\listtext	2	}\expnd0\expndtw0\kerning0
\outl0\strokewidth0 \strokec2 One standardized test (acceleration curve) with onscreen guidance\
\ls3\ilvl0\kerning1\expnd0\expndtw0 \outl0\strokewidth0 {\listtext	3	}\expnd0\expndtw0\kerning0
\outl0\strokewidth0 \strokec2 Photo capture with metadata\
\ls3\ilvl0\kerning1\expnd0\expndtw0 \outl0\strokewidth0 {\listtext	4	}\expnd0\expndtw0\kerning0
\outl0\strokewidth0 \strokec2 Backend receiving and storing runs\
\ls3\ilvl0\kerning1\expnd0\expndtw0 \outl0\strokewidth0 {\listtext	5	}\expnd0\expndtw0\kerning0
\outl0\strokewidth0 \strokec2 Basic comparison view ("how does your run compare to the baseline 2020 Civic?")\
\pard\pardeftab720\sa240\partightenfactor0
\cf0 \strokec2 Then iterate on additional test types and the visualization sophistication.\
Temperature sensor\'97caveat: not all Android phones expose ambient temp reliably, so handle gracefully if it's unavailable. You might want barometric pressure too for altitude context (affects G-force perception slightly).\
\pard\pardeftab720\sa240\partightenfactor0
\cf0 \outl0\strokewidth0 \
}