package com.example.hometec.customview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Matrix
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View

class CustomView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val cardViews = mutableListOf<CardView>()
    private var selectedCardView: CardView? = null
    private var offsetX = 0f
    private var offsetY = 0f
    private var currentShapeType: ShapeType = ShapeType.NONE
    private val gestureDetector: GestureDetector
    private var is3D = false

    init {
        gestureDetector = GestureDetector(context, GestureListener())
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // Draw card views on the canvas
        for (cardView in cardViews) {
            val paint = Paint().apply {
                color = cardView.color
                style = Paint.Style.FILL
            }

            val matrix = Matrix().apply {
                if (cardView.shapeType == ShapeType.WALL) {
                    if (is3D) {
                        // Apply 3D effect: skew the shape to create a 3D appearance
                        setSkew(0.2f, 0.2f, cardView.rect.centerX().toFloat(), cardView.rect.centerY().toFloat())
                    }
                    setRotate(cardView.rotation, cardView.rect.centerX().toFloat(), cardView.rect.centerY().toFloat())
                }
            }

            canvas.save()
            canvas.concat(matrix)
            if (is3D) {
                // Draw a 3D shape with shadow
                paint.color = Color.GRAY
                canvas.drawRect((cardView.rect.left + 10).toFloat(),
                    (cardView.rect.top + 10).toFloat(), (cardView.rect.right + 10).toFloat(),
                    (cardView.rect.bottom + 10).toFloat(), paint)
                paint.color = cardView.color
            }
            canvas.drawRect(cardView.rect, paint)
            canvas.restore()
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        gestureDetector.onTouchEvent(event)
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                selectedCardView = cardViews.find { it.rect.contains(event.x.toInt(), event.y.toInt()) }
                selectedCardView?.let {
                    offsetX = event.x - it.rect.left
                    offsetY = event.y - it.rect.top
                }
            }
            MotionEvent.ACTION_MOVE -> {
                selectedCardView?.let {
                    // Update the position for the selected view, regardless of shape type
                    it.rect.offsetTo((event.x - offsetX).toInt(), (event.y - offsetY).toInt())
                    invalidate()
                }
            }
            MotionEvent.ACTION_UP -> {
                selectedCardView = null
            }
        }
        return true
    }

    fun setCurrentShapeType(shapeType: ShapeType) {
        currentShapeType = shapeType
        addCardView(getCardViewForShapeType(shapeType))
    }

    private fun getCardViewForShapeType(shapeType: ShapeType): CardView {
        val color = when (shapeType) {
            ShapeType.LAND -> Color.GREEN
            ShapeType.ROOM -> Color.BLUE
            ShapeType.WALL -> Color.GRAY
            ShapeType.ARCHITECTURE -> Color.CYAN
            ShapeType.OBJECTS -> Color.MAGENTA
            ShapeType.NONE -> Color.TRANSPARENT
        }

        val rect = when (shapeType) {
            ShapeType.LAND -> Rect(100, 100, 400, 400) // Large square
            ShapeType.ROOM -> Rect(100, 100, 250, 250) // Smaller square
            ShapeType.WALL -> Rect(100, 100, 300, 150) // Straight line
            else -> Rect(0, 0, 0, 0)
        }

        return CardView(
            color = color,
            rect = rect,
            shapeType = shapeType,
            rotation = 0f
        )
    }

    fun resetDrawing() {
        cardViews.clear()
        invalidate()
    }

    fun addCardView(cardView: CardView) {
        cardViews.add(cardView)
        invalidate()
    }

    fun toggle3DMode() {
        is3D = !is3D
        invalidate()
    }

    private inner class GestureListener : GestureDetector.SimpleOnGestureListener() {
        override fun onDoubleTap(event: MotionEvent): Boolean {
            selectedCardView?.let {
                if (it.shapeType == ShapeType.WALL) {
                    it.rotation = (it.rotation + 90) % 360
                    invalidate()
                }
            }
            return true
        }

        override fun onLongPress(event: MotionEvent) {
            selectedCardView?.let {
                if (it.shapeType == ShapeType.WALL) {
                    it.rotation = (it.rotation + 90) % 360
                    invalidate()
                }
            }
        }
    }

    data class CardView(
        val color: Int,
        val rect: Rect,
        val shapeType: ShapeType,
        var rotation: Float // Added rotation property
    )

    enum class ShapeType {
        LAND, ROOM, WALL, ARCHITECTURE, OBJECTS, NONE
    }
}
