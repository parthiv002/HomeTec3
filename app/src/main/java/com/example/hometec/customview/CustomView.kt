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
import android.view.ViewGroup

class CustomView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ViewGroup(context, attrs, defStyleAttr) {

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
        val shapePaint = Paint().apply {
            style = Paint.Style.FILL
        }
        val textPaint = Paint().apply {
            textSize = 30f
            color = Color.BLACK
            style = Paint.Style.FILL
        }

        for (cardView in cardViews) {
            shapePaint.color = cardView.color

            val matrix = Matrix().apply {
                if (cardView.shapeType == ShapeType.WALL) {
                    if (is3D) {
                        setSkew(0.2f, 0.2f, cardView.rect.centerX().toFloat(), cardView.rect.centerY().toFloat())
                    }
                    setRotate(cardView.rotation, cardView.rect.centerX().toFloat(), cardView.rect.centerY().toFloat())
                }
            }

            canvas.save()
            canvas.concat(matrix)
            if (is3D) {
                shapePaint.color = Color.GRAY
                canvas.drawRect(
                    (cardView.rect.left + 10).toFloat(),
                    (cardView.rect.top + 10).toFloat(),
                    (cardView.rect.right + 10).toFloat(),
                    (cardView.rect.bottom + 10).toFloat(),
                    shapePaint
                )
                shapePaint.color = cardView.color
            }
            canvas.drawRect(cardView.rect, shapePaint)

            // Draw dimension lines
            drawDimensionLines(canvas, cardView, textPaint)

            canvas.restore()
        }
    }

    private fun drawDimensionLines(canvas: Canvas, cardView: CardView, textPaint: Paint) {
        val rect = cardView.rect

        // Draw width dimension
        canvas.drawLine(
            rect.left.toFloat(),
            rect.bottom.toFloat() + 20,
            rect.right.toFloat(),
            rect.bottom.toFloat() + 20,
            textPaint
        )
        canvas.drawText(
            "${cardView.width.toInt()}",
            (rect.left + rect.right) / 2f - textPaint.measureText("${cardView.width.toInt()}") / 2,
            rect.bottom.toFloat() + 40,
            textPaint
        )

        // Draw height dimension
        canvas.drawLine(
            rect.right.toFloat() + 20,
            rect.top.toFloat(),
            rect.right.toFloat() + 20,
            rect.bottom.toFloat(),
            textPaint
        )
        canvas.drawText(
            "${cardView.height.toInt()}",
            rect.right.toFloat() + 40,
            (rect.top + rect.bottom) / 2f + textPaint.textSize / 2,
            textPaint
        )
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        // Layout all child views
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            // Example: layout child views in a simple grid
            val left = i * 100
            val top = 0
            val right = left + 100
            val bottom = top + 100
            child.layout(left, top, right, bottom)
        }
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        // Only intercept touch events if the action is a DOWN or MOVE event
        return ev.action == MotionEvent.ACTION_DOWN || ev.action == MotionEvent.ACTION_MOVE
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        gestureDetector.onTouchEvent(event)
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                selectedCardView = cardViews.find { it.rect.contains(event.x.toInt(), event.y.toInt()) }
                selectedCardView?.let {
                    offsetX = event.x - it.rect.left
                    offsetY = event.y - it.rect.top
                    // Prevent parent views from intercepting touch events
                    parent.requestDisallowInterceptTouchEvent(true)
                }
            }
            MotionEvent.ACTION_MOVE -> {
                selectedCardView?.let {
                    it.rect.offsetTo((event.x - offsetX).toInt(), (event.y - offsetY).toInt())
                    // Update the right and bottom coordinates based on the new left and top coordinates
                    it.rect.right = it.rect.left + it.width.toInt()
                    it.rect.bottom = it.rect.top + it.height.toInt()
                    invalidate()
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                selectedCardView = null
                // Allow parent views to intercept touch events again
                parent.requestDisallowInterceptTouchEvent(false)
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
    ) {
        val width: Float
            get() = rect.width().toFloat()

        val height: Float
            get() = rect.height().toFloat()
    }

    enum class ShapeType {
        LAND, ROOM, WALL, ARCHITECTURE, OBJECTS, NONE
    }


}
