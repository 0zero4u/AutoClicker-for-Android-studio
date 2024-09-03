package autoClicker

import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import autoClicker.controller.AddController
import autoClicker.controller.DeleteController
import autoClicker.controller.DragController
import autoClicker.controller.EndController
import autoClicker.controller.StartController
import com.example.autoclicker.R

// Service that handles auto-clicker functions
class AutoClickerService : Service() {

    companion object {
        var isRunning = false
    }

    private lateinit var windowManager: WindowManager
    private lateinit var floatingView: View

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        isRunning = true
        initializeSharedPreferences()
        startAutoClicker()
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun startAutoClicker() {
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        floatingView = LayoutInflater.from(this).inflate(R.layout.auto_clicker_menubar, null)

        val layoutParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                WindowManager.LayoutParams.TYPE_PHONE
            },
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
            PixelFormat.TRANSLUCENT
        ).apply {
            this.x = 0
            this.y = 0
        }

        // classes that manage each AutoClicker menu bar function
        // Sorry for the messy parameters.
        StartController (windowManager, this, floatingView)
        AddController   (windowManager, this, floatingView)
        DeleteController(windowManager, this, floatingView)
        DragController  (windowManager, floatingView, layoutParams)
        EndController   (this, floatingView)

        windowManager.addView(floatingView, layoutParams)
    }

    private fun initializeSharedPreferences() {
        val sharedPrefs = getSharedPreferences("startController", MODE_PRIVATE)
        with(sharedPrefs.edit()) {
            clear()
            apply()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        isRunning = false

        // remove all views (coordinateWidgets, autoClick menuBar)
        for (view in AddController.coordinateStack) {
            windowManager.removeView(view)
        }

        AddController.coordinateStack.clear()
        AddController.counter = 1
        windowManager.removeView(floatingView)
    }
}