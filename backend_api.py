{\rtf1\ansi\ansicpg1252\cocoartf2865
\cocoatextscaling0\cocoaplatform0{\fonttbl\f0\fswiss\fcharset0 Helvetica;}
{\colortbl;\red255\green255\blue255;}
{\*\expandedcolortbl;;}
\margl1440\margr1440\vieww11520\viewh8400\viewkind0
\pard\tx720\tx1440\tx2160\tx2880\tx3600\tx4320\tx5040\tx5760\tx6480\tx7200\tx7920\tx8640\pardirnatural\partightenfactor0

\f0\fs24 \cf0 """\
Suspension Analyzer Backend API\
Flask-based REST API for test run submission and benchmark queries\
"""\
\
from flask import Flask, request, jsonify\
from flask_cors import CORS\
from sqlalchemy import create_engine, Column, Integer, String, Float, DateTime, LargeBinary, func\
from sqlalchemy.ext.declarative import declarative_base\
from sqlalchemy.orm import sessionmaker\
import json\
from datetime import datetime\
import os\
from typing import Optional\
import numpy as np\
\
app = Flask(__name__)\
CORS(app)\
\
# Database setup\
DATABASE_URL = os.getenv('DATABASE_URL', 'sqlite:///./suspension_analyzer.db')\
engine = create_engine(DATABASE_URL, echo=False)\
SessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)\
Base = declarative_base()\
\
# Models\
class VehicleProfile(Base):\
    __tablename__ = "vehicles"\
    \
    id = Column(Integer, primary_key=True, index=True)\
    make = Column(String, index=True)\
    model = Column(String, index=True)\
    year = Column(Integer)\
    suspension_type = Column(String)\
    notes = Column(String, default="")\
    created_at = Column(DateTime, default=datetime.utcnow)\
\
class TestRun(Base):\
    __tablename__ = "test_runs"\
    \
    id = Column(Integer, primary_key=True, index=True)\
    vehicle_id = Column(Integer, index=True)\
    test_type = Column(String, index=True)\
    road_surface = Column(String)\
    protocol_compliance_score = Column(Float)\
    peak_lateral_g = Column(Float)\
    peak_longitudinal_g = Column(Float)\
    peak_vertical_g = Column(Float)\
    rebound_settle_time = Column(Integer)\
    ambient_temp = Column(Float)\
    photo_path = Column(String, nullable=True)\
    sensor_data = Column(String)  # JSON\
    created_at = Column(DateTime, default=datetime.utcnow)\
    user_id = Column(String, default="anonymous")\
\
Base.metadata.create_all(bind=engine)\
\
# API Endpoints\
\
@app.post("/api/v1/vehicles")\
def submit_vehicle_profile():\
    """Register a vehicle profile"""\
    try:\
        data = request.json\
        db = SessionLocal()\
        \
        vehicle = VehicleProfile(\
            make=data.get("make"),\
            model=data.get("model"),\
            year=data.get("year"),\
            suspension_type=data.get("suspensionType"),\
            notes=data.get("notes", "")\
        )\
        db.add(vehicle)\
        db.commit()\
        db.refresh(vehicle)\
        \
        return jsonify(\{\
            "success": True,\
            "data": str(vehicle.id)\
        \}), 201\
    except Exception as e:\
        return jsonify(\{\
            "success": False,\
            "error": str(e)\
        \}), 400\
    finally:\
        db.close()\
\
@app.post("/api/v1/test-runs/submit")\
def submit_test_run():\
    """Submit a test run with sensor data and photo"""\
    try:\
        db = SessionLocal()\
        \
        # Parse JSON data\
        data_str = request.form.get("data")\
        if not data_str:\
            return jsonify(\{"success": False, "error": "Missing data"\}), 400\
        \
        data = json.loads(data_str)\
        \
        # Handle photo upload\
        photo_path = None\
        if "photo" in request.files:\
            photo = request.files["photo"]\
            if photo.filename:\
                photo_dir = "uploads/photos"\
                os.makedirs(photo_dir, exist_ok=True)\
                timestamp = datetime.utcnow().strftime("%Y%m%d_%H%M%S")\
                photo_path = f"\{photo_dir\}/\{timestamp\}_\{photo.filename\}"\
                photo.save(photo_path)\
        \
        # Store test run\
        test_run = TestRun(\
            vehicle_id=int(data.get("vehicleId")),\
            test_type=data.get("testType"),\
            road_surface=data.get("roadSurface"),\
            protocol_compliance_score=float(data.get("protocolComplianceScore", 0)),\
            peak_lateral_g=float(data.get("peakLateralG", 0)),\
            peak_longitudinal_g=float(data.get("peakLongitudinalG", 0)),\
            peak_vertical_g=float(data.get("peakVerticalG", 0)),\
            rebound_settle_time=int(data.get("reboundSettleTime", 0)),\
            ambient_temp=float(data.get("ambientTemp", 0)),\
            photo_path=photo_path,\
            sensor_data=json.dumps(data.get("sensorData", []))\
        )\
        \
        db.add(test_run)\
        db.commit()\
        db.refresh(test_run)\
        \
        return jsonify(\{\
            "success": True,\
            "data": \{\
                "testRunId": str(test_run.id),\
                "message": "Test run submitted successfully"\
            \}\
        \}), 201\
        \
    except Exception as e:\
        return jsonify(\{\
            "success": False,\
            "error": str(e)\
        \}), 400\
    finally:\
        db.close()\
\
@app.get("/api/v1/benchmarks/compare")\
def get_benchmark_comparison():\
    """Get benchmark comparison for a vehicle and test type"""\
    try:\
        make = request.args.get("make")\
        model = request.args.get("model")\
        test_type = request.args.get("testType")\
        peak_lateral_g = float(request.args.get("peakLateralG", 0))\
        \
        db = SessionLocal()\
        \
        # Find baseline vehicle\
        baseline_vehicle = db.query(VehicleProfile).filter(\
            VehicleProfile.make == make,\
            VehicleProfile.model == model\
        ).first()\
        \
        if not baseline_vehicle:\
            return jsonify(\{\
                "success": False,\
                "error": f"No baseline found for \{make\} \{model\}"\
            \}), 404\
        \
        # Get all test runs for this vehicle/test type\
        test_runs = db.query(TestRun).filter(\
            TestRun.vehicle_id == baseline_vehicle.id,\
            TestRun.test_type == test_type\
        ).all()\
        \
        if not test_runs:\
            return jsonify(\{\
                "success": False,\
                "error": f"No test data for \{make\} \{model\} \{test_type\}"\
            \}), 404\
        \
        # Calculate baseline and percentile\
        lateral_g_values = [run.peak_lateral_g for run in test_runs]\
        baseline_lateral_g = np.mean(lateral_g_values)\
        \
        # Calculate percentile (how many are below the submitted value)\
        below_count = sum(1 for g in lateral_g_values if g <= peak_lateral_g)\
        percentile = int((below_count / len(lateral_g_values)) * 100)\
        \
        return jsonify(\{\
            "success": True,\
            "data": \{\
                "vehicleId": str(baseline_vehicle.id),\
                "testType": test_type,\
                "yourPeakLateralG": peak_lateral_g,\
                "baselinePeakLateralG": float(baseline_lateral_g),\
                "baselinePercentile": percentile,\
                "sampleCount": len(test_runs)\
            \}\
        \}), 200\
        \
    except Exception as e:\
        return jsonify(\{\
            "success": False,\
            "error": str(e)\
        \}), 400\
    finally:\
        db.close()\
\
@app.get("/api/v1/benchmarks/vehicle")\
def get_vehicle_benchmarks():\
    """Get aggregate benchmarks for a specific vehicle"""\
    try:\
        vehicle_id = request.args.get("vehicleId")\
        \
        db = SessionLocal()\
        \
        # Get all test runs for this vehicle\
        test_runs = db.query(TestRun).filter(\
            TestRun.vehicle_id == int(vehicle_id)\
        ).all()\
        \
        if not test_runs:\
            return jsonify(\{\
                "success": False,\
                "error": "No test data found"\
            \}), 404\
        \
        # Group by test type and calculate aggregates\
        benchmark_data = []\
        \
        for test_type in set(run.test_type for run in test_runs):\
            runs_for_type = [r for r in test_runs if r.test_type == test_type]\
            \
            lateral_g_values = [r.peak_lateral_g for r in runs_for_type]\
            \
            benchmark_data.append(\{\
                "peakLateralG": float(np.mean(lateral_g_values)),\
                "peakLongitudinalG": float(np.mean([r.peak_longitudinal_g for r in runs_for_type])),\
                "peakVerticalG": float(np.mean([r.peak_vertical_g for r in runs_for_type])),\
                "count": len(runs_for_type),\
                "percentile": 50  # Placeholder\
            \})\
        \
        return jsonify(\{\
            "success": True,\
            "data": benchmark_data\
        \}), 200\
        \
    except Exception as e:\
        return jsonify(\{\
            "success": False,\
            "error": str(e)\
        \}), 400\
    finally:\
        db.close()\
\
@app.get("/api/v1/health")\
def health_check():\
    """Simple health check endpoint"""\
    return jsonify(\{"status": "ok"\}), 200\
\
if __name__ == "__main__":\
    app.run(\
        host="0.0.0.0",\
        port=int(os.getenv("PORT", 5000)),\
        debug=os.getenv("DEBUG", False)\
    )\
```\
\
---\
\
## **File 7: requirements.txt**\
```\
Flask==2.3.2\
Flask-CORS==4.0.0\
SQLAlchemy==2.0.19\
python-dotenv==1.0.0\
numpy==1.24.3\
Werkzeug==2.3.6\
```\
\
---\
\
## **File 8: README.md**\
[Use the README from the previous output\'97it's too long to repeat, but it's available above]\
\
---\
\
## **File 9: IMPLEMENTATION_ISSUES.md**\
[Use the IMPLEMENTATION_ISSUES document from the previous output\'97also comprehensive and too long to repeat]\
\
---\
\
**Quick directory structure to create:**\
```\
suspension_app/\
\uc0\u9500 \u9472 \u9472  SensorDataManager.kt\
\uc0\u9500 \u9472 \u9472  Database.kt\
\uc0\u9500 \u9472 \u9472  TestProtocolManager.kt\
\uc0\u9500 \u9472 \u9472  NetworkClient.kt\
\uc0\u9500 \u9472 \u9472  AndroidManifest.xml\
\uc0\u9500 \u9472 \u9472  backend_api.py\
\uc0\u9500 \u9472 \u9472  requirements.txt\
\uc0\u9500 \u9472 \u9472  README.md\
\uc0\u9492 \u9472 \u9472  IMPLEMENTATION_ISSUES.md\
}