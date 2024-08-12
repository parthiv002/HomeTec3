package com.example.hometec

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.example.hometec.customview.CustomView

class MainActivity : AppCompatActivity() {

    private lateinit var customView: CustomView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        customView = findViewById(R.id.customView)

        val cardLand = findViewById<CardView>(R.id.cardLand)
        val cardRoom = findViewById<CardView>(R.id.cardRoom)
        val cardWall = findViewById<CardView>(R.id.cardWall)
        val cardArchitecture = findViewById<CardView>(R.id.cardArchitecture)
        val cardObjects = findViewById<CardView>(R.id.cardObjects)
        val btnReset = findViewById<Button>(R.id.btnReset)
        val btnSave = findViewById<Button>(R.id.btnSave)
        val btnToggle3D = findViewById<Button>(R.id.btnToggle3D)

        cardLand.setOnClickListener {
            customView.setCurrentShapeType(CustomView.ShapeType.LAND)
        }

        cardRoom.setOnClickListener {
            customView.setCurrentShapeType(CustomView.ShapeType.ROOM)
        }

        cardWall.setOnClickListener {
            customView.setCurrentShapeType(CustomView.ShapeType.WALL)
        }

        cardArchitecture.setOnClickListener {
            customView.setCurrentShapeType(CustomView.ShapeType.ARCHITECTURE)
        }

        cardObjects.setOnClickListener {
            customView.setCurrentShapeType(CustomView.ShapeType.OBJECTS)
        }

        btnReset.setOnClickListener {
            customView.resetDrawing()
        }

        btnSave.setOnClickListener {
            saveDrawingToGallery()
        }

        btnToggle3D.setOnClickListener {
            customView.toggle3DMode()
        }
    }

    private fun saveDrawingToGallery() {
        // Existing save logic
    }
}
