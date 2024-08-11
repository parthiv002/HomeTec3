package com.example.hometec.customview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.RelativeLayout

class CustomView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private var paint = Paint().apply {
        color = Color.GREEN
        style = Paint.Style.STROKE
        strokeWidth = 5f
    }
    private var shapes = mutableListOf<Shape>()
    private var selectedShape: Shape? = null
    private var isScaling = false
    private var lastTouchX = 0f
    private var lastTouchY = 0f
    private var currentShapeType: ShapeType = ShapeType.NONE
    private var isDragging = false
    private var offsetX = 0f
    private var offsetY = 0f

    enum class ShapeType {
        NONE, LAND, ROOM, WALL
    }

    data class Shape(
        var left: Float,
        var top: Float,
        var right: Float,
        var bottom: Float,
        val shapeType: ShapeType,
        val color: Int
    )

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.translate(offsetX, offsetY)

        for (shape in shapes) {
            paint.color = shape.color
            when (shape.shapeType) {
                ShapeType.LAND, ShapeType.ROOM -> {
                    canvas.drawRect(shape.left, shape.top, shape.right, shape.bottom, paint)
                }
                ShapeType.WALL -> {
                    canvas.drawLine(shape.left, shape.top, shape.right, shape.bottom, paint)
                }
                else -> {}
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                lastTouchX = event.x
                lastTouchY = event.y
                selectedShape = findShape(event.x - offsetX, event.y - offsetY)
                isScaling = selectedShape != null
                isDragging = !isScaling
            }
            MotionEvent.ACTION_MOVE -> {
                if (isScaling) {
                    selectedShape?.let {
                        it.right = event.x - offsetX
                        it.bottom = event.y - offsetY
                        invalidate()
                    }
                } else if (isDragging) {
                    offsetX += event.x - lastTouchX
                    offsetY += event.y - lastTouchY
                    lastTouchX = event.x
                    lastTouchY = event.y
                    invalidate()
                }
            }
            MotionEvent.ACTION_UP -> {
                selectedShape = null
                isScaling = false
                isDragging = false
            }
        }
        return true
    }

    fun setCurrentShapeType(type: ShapeType) {
        currentShapeType = type
    }

    fun resetDrawing() {
        shapes.clear()
        invalidate()
    }

    private fun findShape(x: Float, y: Float): Shape? {
        return shapes.find { shape ->
            when (shape.shapeType) {
                ShapeType.LAND, ShapeType.ROOM -> x in shape.left..shape.right && y in shape.top..shape.bottom
                ShapeType.WALL -> {
                    val slope = (shape.bottom - shape.top) / (shape.right - shape.left)
                    val yIntercept = shape.top - slope * shape.left
                    y in (slope * x + yIntercept - 10)..(slope * x + yIntercept + 10)
                }
                else -> false
            }
        }
    }
}