package io.github.jinxiyang.requestpermission.utils

import android.Manifest.permission
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import io.github.jinxiyang.requestpermission.PermissionGroup
import io.github.jinxiyang.requestpermission.PermissionResult

/**
 * 权限Utils
 *
 * 定义了一些常见的权限组，还有很多没定义（懒了）
 */
object PermissionUtils {

    /**
     * 定位权限组
     */
    @JvmField
    val LOCATION_PERMISSIONS = arrayOf(permission.ACCESS_FINE_LOCATION, permission.ACCESS_COARSE_LOCATION)

    /**
     * 存储权限组
     */
    @JvmField
    val STORAGE_PERMISSIONS = arrayOf(permission.READ_EXTERNAL_STORAGE, permission.WRITE_EXTERNAL_STORAGE)

    /**
     * WiFi权限组
     */
    @JvmField
    val WIFI_PERMISSIONS = arrayOf(permission.ACCESS_WIFI_STATE, permission.CHANGE_WIFI_STATE, permission.CHANGE_WIFI_MULTICAST_STATE)

    /**
     * 手机状态权限组
     */
    @JvmField
    val PHONE_STATE_PERMISSIONS = arrayOf(permission.READ_PHONE_STATE)

    /**
     * 读取通讯录权限组
     */
    @JvmField
    val READ_CONTACTS_PERMISSIONS = arrayOf(permission.READ_CONTACTS)

    /**
     * 相机权限组
     */
    @JvmField
    val CAMERA_PERMISSIONS = arrayOf(permission.CAMERA)

    /**
     * 录制音频权限组
     */
    @JvmField
    val RECORD_AUDIO_PERMISSIONS = arrayOf(permission.RECORD_AUDIO)

    /**
     * 判断是否有权限
     */
    @JvmStatic
    fun hasPermission(context: Context, permission: String): Boolean {
        return checkPermissions(context, permission) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * 判断是否有权限
     */
    @JvmStatic
    fun hasPermissions(context: Context, permissions: Array<String>): Boolean {
        permissions.forEach {
            if (!hasPermission(context, it)) {
                return false
            }
        }
        return true
    }

    /**
     * 判断是否有权限
     */
    @JvmStatic
    fun hasPermissions(context: Context, permissions: List<String>): Boolean {
        permissions.forEach {
            if (!hasPermission(context, it)) {
                return false
            }
        }
        return true
    }

    /**
     * 检查权限
     */
    @JvmStatic
    fun checkPermissions(context: Context, permission: String): Int {
        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            // 小于Android 6.0，直接默认允许权限
            PackageManager.PERMISSION_GRANTED
        } else {
            ContextCompat.checkSelfPermission(context, permission)
        }
    }

    /**
     * 判断权限
     */
    @JvmStatic
    fun checkPermissions(context: Context, permissionGroups: List<PermissionGroup>): PermissionResult {
        val result = PermissionResult()
        permissionGroups.forEach {
            it.permissionList.forEach { permission ->
                result.addResult(permission, checkPermissions(context, permission))
            }
        }
        return result
    }
}