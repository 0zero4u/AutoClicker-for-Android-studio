package autoClicker

import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.TextView

// Test clicks per second (not important)
class ClickCounter (
    private val button: Button,
    private val textView: TextView,
    private val updateInterval: Long = 1000L
) {

    private var clickCount = 0
    private var clicksPerSecond = 0
    private val handler = Handler(Looper.getMainLooper())
    private val updateTask = object : Runnable {
        override fun run() {
            clicksPerSecond = clickCount
            textView.text = "Clicks per second: $clicksPerSecond"
            clickCount = 0
            handler.postDelayed(this, updateInterval)
        }
    }

    init {
        button.setOnClickListener {
            increment()
        }
    }

    private fun increment() {
        clickCount++
    }

    fun start() {
        handler.post(updateTask)
    }
}
