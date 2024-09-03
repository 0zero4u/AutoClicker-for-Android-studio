package autoClicker

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

// class that handles permissions
class PermissionHandler (
    private val activity: Activity
) {
    companion object {
        const val REQUEST_CODE_OVERLAY = 100
        const val REQUEST_CODE_POST_NOTIFICATIONS = 101
        const val REQUEST_CODE_ACCESSIBILITY = 102
    }

    private var overlayPermissionGranted = false
    private var postNotificationsPermissionGranted = false
    private var accessibilityPermissionGranted = false

    interface PermissionStatusCallback {
        fun onPermissionsStatusChanged(allPermissionsGranted: Boolean)
    }

    private var callback: PermissionStatusCallback? = null

    fun setPermissionStatusCallback(callback: PermissionStatusCallback) {
        this.callback = callback
    }

    // overlay permission processing
    fun requestOverlayPermission() {
        if (!Settings.canDrawOverlays(activity)) {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:${activity.packageName}")
            )
            activity.startActivityForResult(intent, REQUEST_CODE_OVERLAY)
        } else {
            overlayPermissionGranted = true
            checkPermissions()
        }
    }

    fun overlayPermissionResult(requestCode: Int) {
        if (requestCode == REQUEST_CODE_OVERLAY) {
            if (Settings.canDrawOverlays(activity)) {
                overlayPermissionGranted = true
            } else {
                Toast.makeText(activity, "Overlay permission denied, please again", Toast.LENGTH_SHORT).show()
            }
        }
        if (requestCode == REQUEST_CODE_ACCESSIBILITY) {
            accessibilityPermissionGranted = Settings.Secure.getString(activity.contentResolver, Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES).contains(activity.packageName)
            if (!accessibilityPermissionGranted) {
                Toast.makeText(activity, "Accessibility service denied, Please again", Toast.LENGTH_SHORT).show()
            }
        }
        checkPermissions()
    }

    // post_notifications permission processing
    fun requestPostNotificationsPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(activity, android.Manifest.permission.POST_NOTIFICATIONS)
                != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), REQUEST_CODE_POST_NOTIFICATIONS)
            } else {
                postNotificationsPermissionGranted = true
                checkPermissions()
            }
        } else {
            postNotificationsPermissionGranted = true
            checkPermissions()
        }
    }

    fun postNotificationsPermissionResult(requestCode: Int, grantResults: IntArray) {
        if (requestCode == REQUEST_CODE_POST_NOTIFICATIONS) {
            postNotificationsPermissionGranted = grantResults.isNotEmpty() && grantResults[0] == android.content.pm.PackageManager.PERMISSION_GRANTED
            if (!postNotificationsPermissionGranted) {
                Toast.makeText(activity, "Notification permission denied, please again", Toast.LENGTH_SHORT).show()
            }
            checkPermissions()
        }
    }

    // accessibility permission processing
    fun requestAccessibilityPermission() {
        val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
        activity.startActivityForResult(intent, REQUEST_CODE_ACCESSIBILITY)
    }

    fun checkPermissions() {
        val allPermissionsGranted = overlayPermissionGranted && postNotificationsPermissionGranted && accessibilityPermissionGranted
        callback?.onPermissionsStatusChanged(allPermissionsGranted)
    }
}