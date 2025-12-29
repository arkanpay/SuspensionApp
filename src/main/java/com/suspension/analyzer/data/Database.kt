package com.suspension.analyzer.data

import android.content.Context
import androidx.room.*
import java.io.Serializable

/**
 * Database schema for suspension analyzer app
 */

@Entity(tableName = "vehicles")
data class VehicleProfile(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val make: String,
    val model: String,
    val year: Int,
    val suspensionType: String,  // e.g., "MacPherson strut", "Double wishbone", "Air suspension"
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
) : Serializable

@Entity(tableName = "test_runs")
data class TestRun(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val vehicleId: Int,
    val name: String = "",
    val testType: String,  // e.g., "acceleration_0_40", "lane_change", "deceleration"
    val roadSurface: String,  // e.g., "asphalt_smooth", "concrete", "gravel"
    val roadPhotoPath: String = "",
    val ambientTemp: Float = 0f,
    val pressure: Float = 0f,
    val protocolComplianceScore: Float = 1f,  // 0-1 indicating how well driver followed test curve
    val peakLateralG: Float = 0f,
    val peakLongitudinalG: Float = 0f,
    val peakVerticalG: Float = 0f,
    val reboundSettleTime: Long = 0L,  // milliseconds
    val metricsJson: String = "", // JSON string of metrics
    val score: Double = 0.0,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "test_results")
data class TestResult(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val testRunId: Int,
    val name: String = "",
    val date: Long = System.currentTimeMillis(),
    val rawDataJson: String = "", // JSON string of raw data
    val metricsJson: String = "", // JSON string of metrics
    val score: Double = 0.0
)

@Entity(tableName = "sensor_data")
data class SensorDataPoint(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val testRunId: Int,
    val timestamp: Long,
    val accelX: Float,
    val accelY: Float,
    val accelZ: Float,
    val gyroX: Float,
    val gyroY: Float,
    val gyroZ: Float,
    val pitch: Float,
    val roll: Float,
    val yaw: Float,
    val pressure: Float
)

@Dao
interface VehicleDao {
    @Insert
    suspend fun insertVehicle(vehicle: VehicleProfile): Long

    @Query("SELECT * FROM vehicles ORDER BY createdAt DESC")
    suspend fun getAllVehicles(): List<VehicleProfile>

    @Query("SELECT * FROM vehicles WHERE id = :vehicleId")
    suspend fun getVehicle(vehicleId: Int): VehicleProfile

    @Update
    suspend fun updateVehicle(vehicle: VehicleProfile)

    @Delete
    suspend fun deleteVehicle(vehicle: VehicleProfile)
}

@Dao
interface TestRunDao {
    @Insert
    suspend fun insertTestRun(testRun: TestRun): Long

    @Query("SELECT * FROM test_runs WHERE vehicleId = :vehicleId ORDER BY createdAt DESC")
    suspend fun getTestRunsForVehicle(vehicleId: Int): List<TestRun>

    @Query("SELECT * FROM test_runs WHERE id = :testRunId")
    suspend fun getTestRun(testRunId: Int): TestRun

    @Update
    suspend fun updateTestRun(testRun: TestRun)
}

@Dao
interface TestResultDao {
    @Insert
    suspend fun insertTestResult(result: TestResult): Long

    @Query("SELECT * FROM test_results ORDER BY date DESC")
    suspend fun getAllTestResults(): List<TestResult>

    @Query("SELECT * FROM test_results WHERE id = :id")
    suspend fun getTestResult(id: Int): TestResult

    @Update
    suspend fun updateTestResult(result: TestResult)

    @Delete
    suspend fun deleteTestResult(result: TestResult)
}

@Dao
interface SensorDataDao {
    @Insert
    suspend fun insertSensorDataPoint(point: SensorDataPoint)

    @Insert
    suspend fun insertSensorDataPoints(points: List<SensorDataPoint>)

    @Query("SELECT * FROM sensor_data WHERE testRunId = :testRunId ORDER BY timestamp ASC")
    suspend fun getSensorDataForTestRun(testRunId: Int): List<SensorDataPoint>

    @Query("DELETE FROM sensor_data WHERE testRunId = :testRunId")
    suspend fun deleteSensorDataForTestRun(testRunId: Int)
}


@Database(
    entities = [VehicleProfile::class, TestRun::class, SensorDataPoint::class, TestResult::class],
    version = 2
)
abstract class SuspensionAnalyzerDatabase : RoomDatabase() {
    abstract fun vehicleDao(): VehicleDao
    abstract fun testRunDao(): TestRunDao
    abstract fun sensorDataDao(): SensorDataDao
    abstract fun testResultDao(): TestResultDao

    companion object {
        @Volatile private var instance: SuspensionAnalyzerDatabase? = null

        fun getInstance(context: Context): SuspensionAnalyzerDatabase {
            return instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    SuspensionAnalyzerDatabase::class.java,
                    "suspension_analyzer.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { instance = it }
            }
        }
    }
}

/**
 * Repository pattern for clean data access
 */

class SuspensionRepository(private val database: SuspensionAnalyzerDatabase) {
    // TestResult methods
    suspend fun createTestResult(result: TestResult): Long {
        return database.testResultDao().insertTestResult(result)
    }

    suspend fun getAllTestResults(): List<TestResult> {
        return database.testResultDao().getAllTestResults()
    }

    suspend fun getTestResult(id: Int): TestResult {
        return database.testResultDao().getTestResult(id)
    }

    suspend fun updateTestResult(result: TestResult) {
        database.testResultDao().updateTestResult(result)
    }

    suspend fun deleteTestResult(result: TestResult) {
        database.testResultDao().deleteTestResult(result)
    }

    suspend fun createVehicle(vehicle: VehicleProfile): Long {
        return database.vehicleDao().insertVehicle(vehicle)
    }

    suspend fun getAllVehicles(): List<VehicleProfile> {
        return database.vehicleDao().getAllVehicles()
    }

    suspend fun startTestRun(testRun: TestRun): Long {
        return database.testRunDao().insertTestRun(testRun)
    }

    suspend fun saveSensorData(sensorDataPoints: List<SensorDataPoint>) {
        database.sensorDataDao().insertSensorDataPoints(sensorDataPoints)
    }

    suspend fun completeTestRun(testRun: TestRun) {
        database.testRunDao().updateTestRun(testRun)
    }

    suspend fun getTestRunsForVehicle(vehicleId: Int): List<TestRun> {
        return database.testRunDao().getTestRunsForVehicle(vehicleId)
    }

    suspend fun getSensorDataForTestRun(testRunId: Int): List<SensorDataPoint> {
        return database.sensorDataDao().getSensorDataForTestRun(testRunId)
    }

    // Removed backend upload method
}
