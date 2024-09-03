package autoClicker.controller

import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.view.WindowManager.LayoutParams
import com.example.autoclicker.R

// class responsible for drag function (auto click menu bar)
class DragController (
    private val windowManager: WindowManager,
    private val floatingView: View,
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
        val dragButton = floatingView.findViewById<View>(R.id.dragButton)

        dragButton.setOnTouchListener { view, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> { // when starting touch, saving initial position
                    dragButton.tag = DragData(
                        initialX = layoutParams.x,
                        initialY = layoutParams.y,
                        initialTouchX = event.rawX,
                        initialTouchY = event.rawY
                    )
                    true
                }
                MotionEvent.ACTION_MOVE -> { // when moving, changing view position
                    val dragData = dragButton.tag as? DragData ?: return@setOnTouchListener false
                    layoutParams.x = dragData.initialX + (event.rawX - dragData.initialTouchX).toInt()
                    layoutParams.y = dragData.initialY + (event.rawY - dragData.initialTouchY).toInt()
                    windowManager.updateViewLayout(floatingView, layoutParams)
                    true
                }
                MotionEvent.ACTION_UP -> { // Nothing. idk
                    view.performClick()
                    true
                }
                else -> false
            }
        }
    }
}