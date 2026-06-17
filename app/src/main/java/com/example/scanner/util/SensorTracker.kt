package com.example.scanner.util

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import io.github.sceneview.math.Position
import kotlin.math.cos
import kotlin.math.sin

class SensorTracker(context: Context, private val onStateUpdated: (Position, Float) -> Unit) : SensorEventListener {
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    
    // Quay lại dùng Cảm biến đếm bước chân mặc định của phần cứng
    private val stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)
    // Cảm biến hướng vẫn giữ nguyên
    private val rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)

    private var currentPosition = Position(x = 8f, y = 0.15f, z = 11f) // Bắt đầu ở cửa
    private var currentAzimuth = 0f // Hướng đi (radian)
    private val stepLength = 0.6f // Mỗi bước chân dài 0.6 mét trong model

    // --- CẤU HÌNH ĐỒNG BỘ TỌA ĐỘ (CALIBRATION) ---
    private val xK1 = -13f
    private val zK1 = 11f
    private val xK2 = -9f
    private val zK2 = 11f
    private val mapOffsetAngle = kotlin.math.atan2(xK1 - xK2, -(zK1 - zK2)).toFloat()

    fun startTracking() {
        stepSensor?.let { sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI) }
        rotationSensor?.let { sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI) }
    }

    fun stopTracking() {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event ?: return

        when (event.sensor.type) {
            Sensor.TYPE_ROTATION_VECTOR -> {
                val rotationMatrix = FloatArray(9)
                SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)
                
                val originalOrientation = FloatArray(3)
                SensorManager.getOrientation(rotationMatrix, originalOrientation)
                val pitch = Math.toDegrees(originalOrientation[1].toDouble())

                val remappedMatrix = FloatArray(9)
                
                if (pitch < -45.0 || pitch > 45.0) {
                    SensorManager.remapCoordinateSystem(
                        rotationMatrix,
                        SensorManager.AXIS_X,
                        SensorManager.AXIS_Z,
                        remappedMatrix
                    )
                } else {
                    System.arraycopy(rotationMatrix, 0, remappedMatrix, 0, 9)
                }

                val orientation = FloatArray(3)
                SensorManager.getOrientation(remappedMatrix, orientation)
                
                currentAzimuth = orientation[0]
                val adjustedAzimuth = currentAzimuth + mapOffsetAngle
                
                onStateUpdated(currentPosition, Math.toDegrees(adjustedAzimuth.toDouble()).toFloat())
            }
            
            Sensor.TYPE_STEP_DETECTOR -> {
                // Tính hướng đi đã được đồng bộ với 3D
                val adjustedAzimuth = currentAzimuth + mapOffsetAngle
                
                // Tiến lên theo góc đã đồng bộ
                val dx = stepLength * sin(adjustedAzimuth)
                val dz = -stepLength * cos(adjustedAzimuth)

                currentPosition = Position(
                    x = currentPosition.x + dx,
                    y = currentPosition.y,
                    z = currentPosition.z + dz
                )
                
                onStateUpdated(currentPosition, Math.toDegrees(adjustedAzimuth.toDouble()).toFloat())
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}