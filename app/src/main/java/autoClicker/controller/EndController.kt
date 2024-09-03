package autoClicker.controller

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View
import autoClicker.AutoClickerService
import com.example.autoclicker.R

// class that literally ends AutoClick service (To be precise, delete the auto-clicker menu bar)
class EndController (
    private val context: Context,
    private val floatingView: View
) {
    init {
        setupEndButtonListener()
    }

    private fun setupEndButtonListener() {
        val endButton = floatingView.findViewById<View>(R.id.settingButton)
        endButton.setOnClickListener {

            if (isStartingCheck()) {
                return@setOnClickListener
            }

            val intent = Intent(context, AutoClickerService::class.java)
            context.stopService(intent)
        }
    }

    private fun isStartingCheck(): Boolean {
        val sharedPrefs = context.getSharedPreferences("startController", Context.MODE_PRIVATE)
        return sharedPrefs.getBoolean("start", false)
    }
}