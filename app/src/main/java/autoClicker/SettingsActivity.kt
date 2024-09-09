package autoClicker

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.autoclicker.R

// class that sets button duration and interval
class SettingsActivity : AppCompatActivity() {
    private lateinit var durationEditText: EditText
    private lateinit var delayEditText: EditText
    private lateinit var saveButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.setting)

        durationEditText = findViewById(R.id.durationEditText)
        delayEditText = findViewById(R.id.delayEditText)
        saveButton = findViewById(R.id.saveButton)

        // save data temporarily for reflection in clickSimulation.
        val sharedPrefs = getSharedPreferences("AutoClickerSettings", Context.MODE_PRIVATE)

        durationEditText.setText(sharedPrefs.getLong("duration", 100).toString())
        delayEditText.setText(sharedPrefs.getLong("delayMillis", 100).toString())

        saveButton.setOnClickListener{
            val durationInput = durationEditText.text.toString()
            val delayInput = delayEditText.text.toString()

            // check if invalid
            if (durationInput.isBlank() || delayInput.isBlank()) {
                Toast.makeText(this, "Please enter valid numbers", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val duration = durationInput.toLongOrNull()
            val delayMillis = delayInput.toLongOrNull()

            if (duration == null || delayMillis == null) {
                Toast.makeText(this, "Invalid input. Please enter numbers only.", Toast.LENGTH_SHORT).show()
            } else {
                with(sharedPrefs.edit()) {
                    putLong("duration", duration)
                    putLong("delayMillis", delayMillis)
                    apply()
                }

                // Notify the user
                Toast.makeText(this, "Settings saved", Toast.LENGTH_SHORT).show()

                // Return to MainActivity
                finish()
            }
        }
    }
}