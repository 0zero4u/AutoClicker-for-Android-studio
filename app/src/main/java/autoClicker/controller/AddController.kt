package autoClicker.controller

import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import autoClicker.controller.coordinateAttribute.CoordinateDragController
import com.example.autoclicker.R

// class that add a coordinate widget
class AddController (
    private val windowManager: WindowManager,
    private val context: Context,
    private val floatingView: View
) {
    init {
        setupAddButtonListener()
    }

    companion object {
        val coordinateStack = mutableListOf<View>() // Use the stack to manage widgets.
        var counter = 1 // The counter is a text placed in the middle of a coordinate widget.
    }

    private lateinit var coordinateLayout: View

    private fun setupAddButtonListener() {
        val addButton = floatingView.findViewById<View>(R.id.addButton)
        addButton.setOnClickListener {

            if (isStartingCheck()) {
                return@setOnClickListener
            }

            addCoordinateWidget()
        }
    }

    private fun addCoordinateWidget() {
        coordinateLayout = LayoutInflater.from(context).inflate(R.layout.coordinate_widget, null)
        val layoutParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                WindowManager.LayoutParams.TYPE_PHONE
            },
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        ).apply {
            this.x = 0
            this.y = 0
        }

        // For the coordinate widget to drag
        CoordinateDragController(windowManager, coordinateLayout, layoutParams)

        val coordinateTextView: TextView = coordinateLayout.findViewById(R.id.coordinate_text)
        coordinateTextView.text = counter.toString()

        windowManager.addView(coordinateLayout, layoutParams)
        coordinateStack.add(coordinateLayout)
        counter++
    }

    private fun isStartingCheck(): Boolean {
        val sharedPrefs = context.getSharedPreferences("startController", Context.MODE_PRIVATE)
        return sharedPrefs.getBoolean("start", false)
    }
}
