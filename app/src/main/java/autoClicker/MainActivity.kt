package autoClicker

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.autoclicker.databinding.ActivityMainBinding

// app main
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var permissionHandler: PermissionHandler
    private var allPermissionsGranted = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // No dark mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        // Use to handle permissions easily
        permissionHandler = PermissionHandler(this)
        permissionHandler.setPermissionStatusCallback(object :
            PermissionHandler.PermissionStatusCallback {
            override fun onPermissionsStatusChanged(allPermissionsGranted: Boolean) {
                this@MainActivity.allPermissionsGranted = allPermissionsGranted
            }
        })

        binding.permissionButton.setOnClickListener{
            permissionHandler.requestOverlayPermission()
            permissionHandler.requestPostNotificationsPermission()
            permissionHandler.requestAccessibilityPermission()
        }

        // start button
        binding.startButton.setOnClickListener {
            permissionHandler.checkPermissions()
            if (!AutoClickerService.isRunning) {
                if (allPermissionsGranted) {
                    val intent = Intent(this, AutoClickerService::class.java)
                    startService(intent)
                } else {
                    Toast.makeText(this, "before starting, please check permissions again", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // setting button
        binding.settingButton.setOnClickListener{
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }
    }

    // overlay permission and accessibility permission
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        permissionHandler.overlayPermissionResult(requestCode)
    }

    // post_notifications permission
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionHandler.postNotificationsPermissionResult(requestCode, grantResults)
    }
}