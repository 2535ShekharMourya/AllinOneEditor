package com.example.collageproject.ui.common

import android.content.Context
import android.graphics.Matrix
import android.graphics.PointF
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import androidx.appcompat.widget.AppCompatImageView
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.max
import kotlin.math.sqrt

// Modify the SquareTouchImageView class to handle single clicks vs manipulation

class SquareTouchImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {

    private val matrix = Matrix()
    private val savedMatrix = Matrix()
    private val gestureDetector = GestureDetector(context, GestureListener())

    private val minScale = 1f
    private val maxScale = 5f
    private var currentScale = 1f
    private var currentRotation = 0f

    private val start = PointF()
    private val mid = PointF()
    private var mode = NONE
    private var lastEvent: FloatArray? = null

    // Track last touch time to differentiate between click and manipulation
    private var lastTouchTime: Long = 0
    private val CLICK_TIME_THRESHOLD = 200L // milliseconds to consider as a click

    // Click listener that will be set from outside
    private var onSingleClickListener: OnClickListener? = null

    // Flag to determine if the user is manipulating (dragging/scaling) the image
    private var isManipulating = false

    companion object {
        private const val NONE = 0
        private const val DRAG = 1
        private const val ZOOM = 2
        private const val ZOOM_OR_ROTATE = 3 // Mode for simultaneous zoom/rotate
    }



    init {
        scaleType = ScaleType.MATRIX
    }
    private val scaleDetector =
        ScaleGestureDetector(context, object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
            override fun onScale(detector: ScaleGestureDetector): Boolean {
                val scaleFactor = detector.scaleFactor
                val scale = currentScale * scaleFactor

                if (scale in minScale..maxScale) {
                    currentScale = scale
                    matrix.set(savedMatrix)
                    matrix.postScale(scale, scale, detector.focusX, detector.focusY)
                    imageMatrix = matrix
                    isManipulating = true
                }
                return true
            }
        })
    // Override the original OnClickListener setter to store it internally
    override fun setOnClickListener(l: OnClickListener?) {
        onSingleClickListener = l
        // We don't call super.setOnClickListener because we handle clicks ourselves
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        scaleDetector.onTouchEvent(event)
        gestureDetector.onTouchEvent(event)

        when (event.action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> {
                savedMatrix.set(matrix)
                start.set(event.x, event.y)
                mode = DRAG
                lastEvent = null
                lastTouchTime = System.currentTimeMillis()
                isManipulating = false
            }

            MotionEvent.ACTION_POINTER_DOWN -> {
                val oldDist = spacing(event)
                if (oldDist > 10f) {
                    savedMatrix.set(matrix)
                    midPoint(mid, event)
                    mode = ZOOM_OR_ROTATE // Set mode to handle both zoom and rotate
                    lastEvent = FloatArray(4).apply {
                        set(0, event.getX(0))
                        set(1, event.getX(1))
                        set(2, event.getY(0))
                        set(3, event.getY(1))
                    }
                    isManipulating = true
                }
            }

            MotionEvent.ACTION_UP -> {
                mode = NONE
                lastEvent = null

                // Check if this was a click (short touch without much movement)
                val touchDuration = System.currentTimeMillis() - lastTouchTime
                if (touchDuration < CLICK_TIME_THRESHOLD && !isManipulating) {
                    // This was a click - call the onClick listener
                    onSingleClickListener?.onClick(this)
                    return true
                }
            }

            MotionEvent.ACTION_POINTER_UP -> {
                mode = NONE
                lastEvent = null
            }

            MotionEvent.ACTION_MOVE -> {
                if (mode == DRAG && !scaleDetector.isInProgress) {
                    // Check if the user has moved enough to consider it a drag
                    val dx = abs(event.x - start.x)
                    val dy = abs(event.y - start.y)

                    // If movement is significant, it's a drag operation
                    if (dx > 10 || dy > 10) {
                        isManipulating = true
                    }

                    matrix.set(savedMatrix)
                    matrix.postTranslate(event.x - start.x, event.y - start.y)
                    imageMatrix = matrix
                } else if (mode == ZOOM_OR_ROTATE && event.pointerCount == 2 && lastEvent != null) {
                    // Handle zooming
                    val newDist = spacing(event)
                    if (newDist > 10f) {
                        val scale = newDist / spacing(lastEvent!!)
                        matrix.set(savedMatrix)
                        matrix.postScale(scale, scale, mid.x, mid.y)
                    }

                    // Handle rotation
                    val newRotation = rotation(event)
                    val deltaRotation = newRotation - rotation(lastEvent!!)
                    matrix.postRotate(deltaRotation, mid.x, mid.y)
                    imageMatrix = matrix
                    isManipulating = true
                }
            }
        }
        return true
    }

    private fun spacing(event: MotionEvent): Float {
        val x = event.getX(0) - event.getX(1)
        val y = event.getY(0) - event.getY(1)
        return sqrt(x * x + y * y)
    }

    private fun spacing(points: FloatArray): Float {
        val x = points[0] - points[1]
        val y = points[2] - points[3]
        return sqrt(x * x + y * y)
    }

    private fun midPoint(point: PointF, event: MotionEvent) {
        val x = event.getX(0) + event.getX(1)
        val y = event.getY(0) + event.getY(1)
        point.set(x / 2, y / 2)
    }

    private fun rotation(event: MotionEvent): Float {
        val deltaX = (event.getX(0) - event.getX(1)).toDouble()
        val deltaY = (event.getY(0) - event.getY(1)).toDouble()
        return Math.toDegrees(atan2(deltaY, deltaX)).toFloat()
    }

    private fun rotation(points: FloatArray): Float {
        val deltaX = (points[0] - points[1]).toDouble()
        val deltaY = (points[2] - points[3]).toDouble()
        return Math.toDegrees(atan2(deltaY, deltaX)).toFloat()
    }

    fun setupInitialMatrix() {
        val drawable = drawable ?: return
        val viewWidth = width.toFloat()
        val viewHeight = height.toFloat()
        val drawableWidth = drawable.intrinsicWidth.toFloat()
        val drawableHeight = drawable.intrinsicHeight.toFloat()

        if (viewWidth <= 0 || viewHeight <= 0 || drawableWidth <= 0 || drawableHeight <= 0) {
            return
        }
        // Calculate scale to fit the image within the view bounds
        val scaleX = viewWidth / drawableWidth
        val scaleY = viewHeight / drawableHeight
        val scale = maxOf(scaleX, scaleY) // Use min to ensure image fits within bounds

        // Center the image
        val dx = (viewWidth - drawableWidth * scale) * 0.5f
        val dy = (viewHeight - drawableHeight * scale) * 0.5f

        matrix.reset()
        matrix.setScale(scale, scale)
        matrix.postTranslate(dx, dy)
        imageMatrix = matrix
        currentScale = scale
    }

    fun reset() {
        matrix.reset()
        imageMatrix = matrix
        currentScale = 1f
        currentRotation = 0f
    }

    inner class GestureListener : GestureDetector.SimpleOnGestureListener() {
//        override fun onDoubleTap(e: MotionEvent): Boolean {
//            // Zoom in or out on double-tap
//            matrix.postScale(1.5f, 1.5f, e.x, e.y)
//            imageMatrix = matrix
//            isManipulating = true
//            return true
//        }
    }
}