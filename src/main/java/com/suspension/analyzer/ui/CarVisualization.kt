package com.suspension.analyzer.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import kotlin.math.cos
import kotlin.math.sin

/**
 * Custom view that renders a 3D wireframe car model
 * Updates in real-time based on phone orientation (pitch, roll, yaw)
 */
class CarVisualization @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    // Suspension score overlay
    private var score: Double? = null
    private val scorePaint = Paint().apply {
        color = Color.argb(200, 30, 30, 30)
        style = Paint.Style.FILL
        isAntiAlias = true
    }
    private val scoreTextPaint = Paint().apply {
        color = Color.WHITE
        textSize = 48f
        isAntiAlias = true
        setShadowLayer(4f, 2f, 2f, Color.BLACK)
    }

    fun setScore(score: Double?) {
        this.score = score
        invalidate()
    }

    private val paint = Paint().apply {
        color = Color.WHITE
        strokeWidth = 3f
        style = Paint.Style.STROKE
        isAntiAlias = true
    }

    private val fillPaint = Paint().apply {
        color = Color.parseColor("#4444FF")
        style = Paint.Style.FILL
        alpha = 100
    }

    // Current orientation angles in radians
    private var pitch = 0f  // Forward/backward tilt
    private var roll = 0f   // Left/right tilt
    private var yaw = 0f    // Rotation around vertical axis

    // 3D Car model vertices (simplified box model)
    // X = width, Y = height, Z = depth
    private val carVertices = listOf(
        // Bottom face
        Triple(-1f, -0.5f, -2f),  // 0: Front-left-bottom
        Triple(1f, -0.5f, -2f),   // 1: Front-right-bottom
        Triple(1f, -0.5f, 2f),    // 2: Back-right-bottom
        Triple(-1f, -0.5f, 2f),   // 3: Back-left-bottom

        // Top face
        Triple(-1f, 0.5f, -2f),   // 4: Front-left-top
        Triple(1f, 0.5f, -2f),    // 5: Front-right-top
        Triple(1f, 0.5f, 2f),     // 6: Back-right-top
        Triple(-1f, 0.5f, 2f),    // 7: Back-left-top

        // Cabin (roof)
        Triple(-0.7f, 0.5f, -0.5f), // 8: Cabin front-left
        Triple(0.7f, 0.5f, -0.5f),  // 9: Cabin front-right
        Triple(0.7f, 0.5f, 1.2f),   // 10: Cabin back-right
        Triple(-0.7f, 0.5f, 1.2f),  // 11: Cabin back-left

        Triple(-0.7f, 1.2f, -0.5f), // 12: Cabin roof front-left
        Triple(0.7f, 1.2f, -0.5f),  // 13: Cabin roof front-right
        Triple(0.7f, 1.2f, 1.2f),   // 14: Cabin roof back-right
        Triple(-0.7f, 1.2f, 1.2f),  // 15: Cabin roof back-left
    )

    // Define edges connecting vertices
    private val carEdges = listOf(
        // Bottom face
        Pair(0, 1), Pair(1, 2), Pair(2, 3), Pair(3, 0),
        // Top face
        Pair(4, 5), Pair(5, 6), Pair(6, 7), Pair(7, 4),
        // Vertical edges
        Pair(0, 4), Pair(1, 5), Pair(2, 6), Pair(3, 7),

        // Cabin bottom
        Pair(8, 9), Pair(9, 10), Pair(10, 11), Pair(11, 8),
        // Cabin top
        Pair(12, 13), Pair(13, 14), Pair(14, 15), Pair(15, 12),
        // Cabin vertical
        Pair(8, 12), Pair(9, 13), Pair(10, 14), Pair(11, 15),
    )

    fun updateOrientation(pitchRad: Float, rollRad: Float, yawRad: Float) {
        this.pitch = pitchRad
        this.roll = rollRad
        this.yaw = yawRad
        invalidate()  // Trigger redraw
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val centerX = width / 2f
        val centerY = height / 2f
        val scale = 80f  // Scale factor for rendering

        // Project 3D vertices to 2D screen coordinates
        val projectedVertices = carVertices.map { vertex ->
            project3DTo2D(vertex, pitch, roll, yaw, centerX, centerY, scale)
        }

        // Draw filled faces first (for depth)
        drawFilledFaces(canvas, projectedVertices)

        // Draw edges
        carEdges.forEach { (startIdx, endIdx) ->
            val start = projectedVertices[startIdx]
            val end = projectedVertices[endIdx]
            if (start != null && end != null) {
                canvas.drawLine(start.first, start.second, end.first, end.second, paint)
            }
        }

        // Draw orientation indicators
        drawOrientationAxes(canvas, centerX, centerY, scale)

        // Draw score overlay (top-right corner)
        score?.let {
            val scoreText = "Score: %.1f".format(it)
            val padding = 24f
            val rectWidth = scoreTextPaint.measureText(scoreText) + 2 * padding
            val rectHeight = 70f
            val left = width - rectWidth - padding
            val top = padding
            // Draw background
            canvas.drawRoundRect(left, top, left + rectWidth, top + rectHeight, 20f, 20f, scorePaint)
            // Draw text
            canvas.drawText(scoreText, left + padding, top + rectHeight / 1.7f, scoreTextPaint)
        }
    }

    private fun drawFilledFaces(canvas: Canvas, vertices: List<Pair<Float, Float>?>) {
        // Draw bottom face
        drawQuad(canvas, vertices, listOf(0, 1, 2, 3), fillPaint)

        // Draw top face
        drawQuad(canvas, vertices, listOf(4, 5, 6, 7), fillPaint)

        // Draw front face
        drawQuad(canvas, vertices, listOf(0, 1, 5, 4), fillPaint)

        // Draw back face
        drawQuad(canvas, vertices, listOf(3, 2, 6, 7), fillPaint)
    }

    private fun drawQuad(
        canvas: Canvas,
        vertices: List<Pair<Float, Float>?>,
        indices: List<Int>,
        paint: Paint
    ) {
        if (indices.all { vertices[it] != null }) {
            val path = Path().apply {
                val start = vertices[indices[0]]!!
                moveTo(start.first, start.second)
                indices.drop(1).forEach { idx ->
                    val point = vertices[idx]!!
                    lineTo(point.first, point.second)
                }
                close()
            }
            canvas.drawPath(path, paint)
        }
    }

    private fun drawOrientationAxes(canvas: Canvas, centerX: Float, centerY: Float, scale: Float) {
        // Draw small orientation axes at bottom
        val axisLength = 50f
        val axisY = height - 100f

        // X axis (red) - roll
        val xAxisPaint = Paint().apply {
            color = Color.RED
            strokeWidth = 4f
        }
        canvas.drawLine(centerX, axisY, centerX + axisLength * cos(roll), axisY - axisLength * sin(roll), xAxisPaint)

        // Y axis (green) - pitch
        val yAxisPaint = Paint().apply {
            color = Color.GREEN
            strokeWidth = 4f
        }
        canvas.drawLine(centerX, axisY, centerX + axisLength * cos(pitch), axisY - axisLength * sin(pitch), yAxisPaint)

        // Z axis (blue) - yaw indicator (circle)
        val zAxisPaint = Paint().apply {
            color = Color.BLUE
            strokeWidth = 4f
            style = Paint.Style.STROKE
        }
        canvas.drawCircle(centerX, axisY, 30f, zAxisPaint)
        canvas.drawLine(
            centerX,
            axisY,
            centerX + 30f * sin(yaw),
            axisY - 30f * cos(yaw),
            zAxisPaint
        )
    }

    /**
     * Projects a 3D point to 2D screen coordinates using rotation matrices
     */
    private fun project3DTo2D(
        point: Triple<Float, Float, Float>,
        pitch: Float,
        roll: Float,
        yaw: Float,
        centerX: Float,
        centerY: Float,
        scale: Float
    ): Pair<Float, Float>? {
        var (x, y, z) = point

        // Apply yaw rotation (around Y axis)
        val cosYaw = cos(yaw)
        val sinYaw = sin(yaw)
        val xYaw = x * cosYaw + z * sinYaw
        val zYaw = -x * sinYaw + z * cosYaw
        x = xYaw
        z = zYaw

        // Apply pitch rotation (around X axis)
        val cosPitch = cos(pitch)
        val sinPitch = sin(pitch)
        val yPitch = y * cosPitch - z * sinPitch
        val zPitch = y * sinPitch + z * cosPitch
        y = yPitch
        z = zPitch

        // Apply roll rotation (around Z axis)
        val cosRoll = cos(roll)
        val sinRoll = sin(roll)
        val xRoll = x * cosRoll - y * sinRoll
        val yRoll = x * sinRoll + y * cosRoll
        x = xRoll
        y = yRoll

        // Perspective projection
        val distance = 8f
        val zProj = z + distance

        if (zProj <= 0) return null  // Behind camera

        val perspective = distance / zProj
        val screenX = centerX + x * scale * perspective
        val screenY = centerY - y * scale * perspective

        return Pair(screenX, screenY)
    }
}
