package autoClicker.controller

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import autoClicker.ClickSimulation
import com.example.autoclicker.R

// when there is at least one coordinate widget.
class StartController (
    private val windowManager: WindowManager,
    private val context: Context,
    private val floatingView: View,
) {
    private var isStarting = false
    private val coordinateStack = AddController.coordinateStack

    init {
        setupStartButtonListener()
    }

    private fun setupStartButtonListener() {
        val startButton = floatingView.findViewById<View>(R.id.startButton)
        startButton.setOnClickListener {
            // if no coordinate widget, no action should be made.
            if (coordinateStack.isEmpty()) {
                return@setOnClickListener
            }

            if (!isStarting) {
                isStarting = true
                setTouchableFlagForAllViews(false)
                startClickSimulationService()
                changeMenuBarColor(true)
                changeWidgetsBackgroundColor(true)
            } else {
                isStarting = false
                setTouchableFlagForAllViews(true)
                stopClickSimulationService()
                changeMenuBarColor(false)
                changeWidgetsBackgroundColor(false)
            }

            // When running, the rest should have not to operate except for the start button!
            // Of course, when running,
            // it was possible to give the effect of pressing the start button when clicking another button...
            val sharedPrefs = context.getSharedPreferences("startController", Context.MODE_PRIVATE)
            with(sharedPrefs.edit()) {
                putBoolean("start", isStarting)
                apply()
            }
            Log.d("test", "edit = ${sharedPrefs.getBoolean("start", false)}")
        }
    }

    // When running, all coordinate widgets must have the properties of FLAG_NOT_TOUCHABLE,
    // otherwise it will intercept touch events.
    // However, if it has that property, it can't be dragged, so it will be removed when isStarting is false.
    private fun setTouchableFlagForAllViews(isTouchable: Boolean) {
        for (view in coordinateStack) {
            val layoutParams = view.layoutParams as? WindowManager.LayoutParams
            if (layoutParams != null) {
                layoutParams.flags = if (isTouchable) {
                    layoutParams.flags and WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE.inv()
                } else {
                    layoutParams.flags or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                }
                windowManager.updateViewLayout(view, layoutParams)
            }
        }
    }

    private fun startClickSimulationService() {
        val intent = Intent(context.applicationContext, ClickSimulation::class.java)
        context.applicationContext.startService(intent)
    }

    private fun stopClickSimulationService() {
        val intent = Intent(context.applicationContext, ClickSimulation::class.java)
        intent.action = "STOP" // Define a custom action for stopping the service
        context.applicationContext.startService(intent)
    }

    private fun changeMenuBarColor(isStarting: Boolean) {
        val menuBar = floatingView.findViewById<View>(R.id.menuBar)
        val drawable = menuBar.background as GradientDrawable

        val backgroundColor = if (isStarting) {
            context.getColor(R.color.yellow_green)
        } else {
            Color.TRANSPARENT
        }
        drawable.setColor(backgroundColor)
    }

    private fun changeWidgetsBackgroundColor(isStarting: Boolean) {
        val backgroundColor = if (isStarting) {
            context.getColor(R.color.yellow_green)
        } else {
            Color.TRANSPARENT
        }

        for (view in AddController.coordinateStack) {
            val imageView = view.findViewById<ImageView>(R.id.imageView)
            val drawable = GradientDrawable()
            drawable.shape = GradientDrawable.OVAL
            drawable.setColor(backgroundColor)
            imageView.background = drawable
        }
    }
}