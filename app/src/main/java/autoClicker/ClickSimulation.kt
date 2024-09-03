package autoClicker

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.accessibilityservice.GestureDescription.StrokeDescription
import android.content.Context
import android.content.Intent
import android.graphics.Path
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import autoClicker.controller.AddController

// ★ class that implements auto-click
class ClickSimulation : AccessibilityService() {
    private var handler: Handler? = null
    private var runnable: IntervalRunnable? = null
    private val coordinateStack = AddController.coordinateStack
    private var currentClickIndex = 0
    private var isRunning = false
    private var duration : Long = 100
    private var delayMillis : Long = 100

    private inner class IntervalRunnable: Runnable {
        override fun run() {
            if (isRunning) {
                val view = AddController.coordinateStack[currentClickIndex]
                val location = IntArray(2)
                view.getLocationOnScreen(location)
                val x = location[0] + view.width / 2
                val y = location[1] + view.height / 2

                simulateClick(x, y)
                currentClickIndex = (currentClickIndex + 1) % coordinateStack.size
                handler?.postDelayed(this, delayMillis + duration)
            }
        }
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {}
    override fun onInterrupt() {}

    override fun onCreate() {
        super.onCreate()
        val handlerThread = HandlerThread("name is name")
        handlerThread.start()
        handler = Handler(handlerThread.looper)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // get the two saved values from the SettingsActivity.
        val sharedPrefs = getSharedPreferences("AutoClickerSettings", Context.MODE_PRIVATE)
        duration = sharedPrefs.getLong("duration", 100)
        delayMillis = sharedPrefs.getLong("delayMillis", 100)
        Log.d("test", "Loaded settings: Duration=$duration, DelayMillis=$delayMillis")

        intent?.let {
            if (it.action == "STOP") {
                stopClickSimulation()
                return START_NOT_STICKY
            }

            if (!isRunning) {
                isRunning = true
                runnable = IntervalRunnable()
                runnable?.let { runnable -> handler?.post(runnable) }
            }
        }
        return START_NOT_STICKY
    }

    private fun simulateClick(x: Int, y: Int) {
        val path = Path().apply {
            moveTo(x.toFloat(), y.toFloat())
            lineTo(x.toFloat(), y.toFloat())
        }

        val gesture = GestureDescription.Builder().apply {
            addStroke(StrokeDescription(path, 0, duration))
        }

        Log.d("test", "x:$x | y:$y")
        dispatchGesture(gesture.build(), object : GestureResultCallback() {
            override fun onCompleted(gestureDescription: GestureDescription) {
                super.onCompleted(gestureDescription)
            }

            override fun onCancelled(gestureDescription: GestureDescription) {
                super.onCancelled(gestureDescription)
            }
        }, null)
    }

    private fun stopClickSimulation() {
        isRunning = false
        handler?.removeCallbacksAndMessages(null)
        stopSelf()
    }
}