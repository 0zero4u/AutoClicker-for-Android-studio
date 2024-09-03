package autoClicker.controller.coordinateAttribute

import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.view.WindowManager.LayoutParams
import com.example.autoclicker.R

// The coordinate widget has to be movable, right?
class CoordinateDragController (
    private val windowManager: WindowManager,
    private val coordinateLayout: View,
    private val layoutParams: LayoutParams
) {
    init {
        setDragTouchListener()
    }

    data class DragData(
        val initialX: Int,
        val initialY: Int,
        val initialTouchX: Float,
        val initialTouchY: Float
    )

    private fun setDragTouchListener() {
        val coordinateWidget = coordinateLayout.findViewById<View>(R.id.coordinate)

        coordinateWidget.setOnTouchListener { view, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    coordinateWidget.tag = DragData(
                        initialX = layoutParams.x,
                        initialY = layoutParams.y,
                        initialTouchX = event.rawX,
                        initialTouchY = event.rawY
                    )
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    val dragData = coordinateWidget.tag as? DragData ?: return@setOnTouchListener false
                    layoutParams.x = dragData.initialX + (event.rawX - dragData.initialTouchX).toInt()
                    layoutParams.y = dragData.initialY + (event.rawY - dragData.initialTouchY).toInt()
                    windowManager.updateViewLayout(coordinateLayout, layoutParams)
                    true
                }
                MotionEvent.ACTION_UP -> {
                    view.performClick()
                    true
                }
                else -> false
            }
        }
    }
}