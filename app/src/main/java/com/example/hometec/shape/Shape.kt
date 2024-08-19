package com.example.hometec.shape

import com.example.hometec.customview.CustomView

data class Shape(
    var left: Float,
    var top: Float,
    var right: Float,
    var bottom: Float,
    val shapeType: CustomView.ShapeType,
    val color: Int,
    var isSelected: Boolean = false
) {
    // Compute width and height
    val width: Float
        get() = right - left

    val height: Float
        get() = bottom - top
}
