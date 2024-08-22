package com.example.hometec

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.view.MotionEvent
import com.example.hometec.customview.CustomView

class MyParameters(private val customView: CustomView) {

    private val paint = Paint().apply {
        color = Color.RED
        style = Paint.Style.STROKE
        strokeWidth = 5f
    }
    private val pointPaint = Paint().apply {
        color = Color.YELLOW
        style = Paint.Style.FILL
    }
    private var selectedCorner: Corner? = null
    private val cornerSize = 20

    fun drawPerimeter(canvas: Canvas, rect: Rect) {
        // Draw red perimeter
        canvas.drawRect(rect, paint)
        drawResizeHandles(canvas, rect)

        // Draw corner points
        drawCornerPoint(canvas, rect.left, rect.top)
        drawCornerPoint(canvas, rect.right, rect.top)
        drawCornerPoint(canvas, rect.left, rect.bottom)
        drawCornerPoint(canvas, rect.right, rect.bottom)
    }

    private fun drawResizeHandles(canvas: Canvas, rect: Rect) {
        canvas.drawRect(
            (rect.left - cornerSize).toFloat(), (rect.top - cornerSize).toFloat(),
            (rect.left + cornerSize).toFloat(), (rect.top + cornerSize).toFloat(),
            paint
        )
        canvas.drawRect(
            (rect.right - cornerSize).toFloat(), (rect.top - cornerSize).toFloat(),
            (rect.right + cornerSize).toFloat(), (rect.top + cornerSize).toFloat(),
            paint
        )
        canvas.drawRect(
            (rect.left - cornerSize).toFloat(), (rect.bottom - cornerSize).toFloat(),
            (rect.left + cornerSize).toFloat(), (rect.bottom + cornerSize).toFloat(),
            paint
        )
        canvas.drawRect(
            (rect.right - cornerSize).toFloat(), (rect.bottom - cornerSize).toFloat(),
            (rect.right + cornerSize).toFloat(), (rect.bottom + cornerSize).toFloat(),
            paint
        )
    }

    private fun drawCornerPoint(canvas: Canvas, x: Int, y: Int) {
        canvas.drawCircle(x.toFloat(), y.toFloat(), 15f, pointPaint)
    }

    fun handleTouch(event: MotionEvent, rect: Rect): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                selectedCorner = detectCorner(event.x, event.y, rect)
            }
            MotionEvent.ACTION_MOVE -> {
                selectedCorner?.let {
                    resizeShape(it, event.x, event.y, rect)
                    customView.invalidate()
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                selectedCorner = null
            }
        }
        return selectedCorner != null
    }

    private fun detectCorner(x: Float, y: Float, rect: Rect): Corner? {
        return when {
            isWithinRange(x, y, rect.left, rect.top) -> Corner.TOP_LEFT
            isWithinRange(x, y, rect.right, rect.top) -> Corner.TOP_RIGHT
            isWithinRange(x, y, rect.left, rect.bottom) -> Corner.BOTTOM_LEFT
            isWithinRange(x, y, rect.right, rect.bottom) -> Corner.BOTTOM_RIGHT
            else -> null
        }
    }

    private fun isWithinRange(x: Float, y: Float, cx: Int, cy: Int): Boolean {
        val threshold = 30
        return (x - cx).toInt() in -threshold..threshold && (y - cy).toInt() in -threshold..threshold
    }

    private fun resizeShape(corner: Corner, x: Float, y: Float, rect: Rect) {
        when (corner) {
            Corner.TOP_LEFT -> {
                rect.left = x.toInt()
                rect.top = y.toInt()
            }
            Corner.TOP_RIGHT -> {
                rect.right = x.toInt()
                rect.top = y.toInt()
            }
            Corner.BOTTOM_LEFT -> {
                rect.left = x.toInt()
                rect.bottom = y.toInt()
            }
            Corner.BOTTOM_RIGHT -> {
                rect.right = x.toInt()
                rect.bottom = y.toInt()
            }
        }
    }

    private enum class Corner {
        TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT
    }
}
