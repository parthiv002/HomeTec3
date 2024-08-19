package com.example.hometec

import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.example.hometec.customview.CustomView
import java.io.OutputStream

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
        // Create a bitmap of the CustomView
        val bitmap = Bitmap.createBitmap(customView.width, customView.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        customView.draw(canvas)

        val filename = "CustomDrawing_${System.currentTimeMillis()}.png"
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, filename)
            put(MediaStore.Images.Media.MIME_TYPE, "image/png")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CustomDrawings")
                put(MediaStore.Images.Media.IS_PENDING, 1)
            }
        }

        val resolver = contentResolver
        val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        uri?.let {
            var outputStream: OutputStream? = null
            try {
                outputStream = resolver.openOutputStream(uri)
                if (outputStream != null) {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                }
                outputStream?.close()

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    contentValues.clear()
                    contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
                    resolver.update(uri, contentValues, null, null)
                }

                Toast.makeText(this, "Drawing saved to gallery", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this, "Failed to save drawing", Toast.LENGTH_SHORT).show()
            } finally {
                outputStream?.close()
            }
        } ?: run {
            Toast.makeText(this, "Failed to create new MediaStore record", Toast.LENGTH_SHORT).show()
        }
    }
}

