package com.example.hometec

import android.graphics.Color
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
            // If you want to handle Architecture or Objects differently, you can
        }

        cardObjects.setOnClickListener {
            // If you want to handle Architecture or Objects differently, you can
        }

        btnReset.setOnClickListener {
            customView.resetDrawing()
        }
    }
}