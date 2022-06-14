package io.github.jinxiyang.requestpermission.utils

import android.Manifest.permission
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat

object PermissionUtils {

    @JvmField
    val LOCATION_PERMISSIONS = arrayOf(permission.ACCESS_FINE_LOCATION, permission.ACCESS_COARSE_LOCATION)

    @JvmField
    val STORAGE_PERMISSIONS = arrayOf(permission.READ_EXTERNAL_STORAGE, permission.WRITE_EXTERNAL_STORAGE)

    @JvmField
    val WIFI_PERMISSIONS = arrayOf(permission.ACCESS_WIFI_STATE, permission.CHANGE_WIFI_STATE, permission.CHANGE_WIFI_MULTICAST_STATE)

    @JvmField
    val PHONE_STATE_PERMISSIONS = arrayOf(permission.READ_PHONE_STATE)

    @JvmField
    val READ_CONTACTS_PERMISSIONS = arrayOf(permission.READ_CONTACTS)

    @JvmField
    val CAMERA_PERMISSIONS = arrayOf(permission.CAMERA)

    @JvmField
    val RECORD_AUDIO_PERMISSIONS = arrayOf(permission.RECORD_AUDIO)

    /**
     * 判断是否有权限
     */
    @JvmStatic
    fun hasPermission(context: Context, permission: String): Boolean {
        val permissions = mutableListOf<String>()
        permissions.add(permission)
        return hasPermissions(context, permissions)
    }

    /**
     * 判断是否有权限
     */
    @JvmStatic
    fun hasPermissions(context: Context, permissions: Array<String>): Boolean {
        return hasPermissions(context, permissions.toList())
    }

    /**
     * 判断是否有权限
     */
    @JvmStatic
    fun hasPermissions(context: Context, permissions: List<String>, hasNotPermissionList: MutableList<String>? = null): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            // 小于Android 6.0，直接默认允许权限
            return true
        }

        val hasNotPermissions: MutableList<String> = hasNotPermissionList ?: mutableListOf()
        permissions.forEach {
            if (ContextCompat.checkSelfPermission(context, it) != PackageManager.PERMISSION_GRANTED) {
                hasNotPermissions.add(it)
            }
        }
        return hasNotPermissions.isEmpty()
    }
}