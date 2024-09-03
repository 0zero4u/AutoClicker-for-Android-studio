package autoClicker.controller

import android.content.Context
import android.view.View
import android.view.WindowManager
import com.example.autoclicker.R

// class that deletes the latest created coordinate widget
class DeleteController (
    private val windowManager: WindowManager,
    private val context: Context,
    private val floatingView: View
) {

    init {
        setupDeleteButtonListener()
    }

    private fun setupDeleteButtonListener() {
        val deleteButton = floatingView.findViewById<View>(R.id.deleteButton)
        deleteButton.setOnClickListener {

            if (isStartingCheck()) {
                return@setOnClickListener
            }
            removeLatestCoordinate()
        }
    }

    private fun removeLatestCoordinate() {
        if (AddController.coordinateStack.isNotEmpty()) {
            val viewToRemove = AddController.coordinateStack.removeAt(AddController.coordinateStack.size - 1)
            windowManager.removeView(viewToRemove)
            AddController.counter--
        }
    }

    private fun isStartingCheck(): Boolean {
        val sharedPrefs = context.getSharedPreferences("startController", Context.MODE_PRIVATE)
        return sharedPrefs.getBoolean("start", false)
    }
}