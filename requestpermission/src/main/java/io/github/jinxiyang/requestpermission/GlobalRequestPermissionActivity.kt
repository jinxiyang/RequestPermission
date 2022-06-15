package io.github.jinxiyang.requestpermission

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import io.github.jinxiyang.requestpermission.utils.PermissionUtils
import io.github.jinxiyang.requestpermission.utils.StatusBarUtils

/**
 * 公用的请求权限中转页面，默认背景透明，看起来不存在一样。
 *
 * 如果想实现自定义权限中转页面，可以继承此activity
 */
open class GlobalRequestPermissionActivity : AppCompatActivity() {

    private val mPermissionGroupList: MutableList<PermissionGroup> = mutableListOf()

    private val mPermissionResult: PermissionResult = PermissionResult()

    private lateinit var mRequestingPermissionGroup: PermissionGroup

    private var mRequestCode: Int = 0x123

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initParam()
        initView(mPermissionGroupList)
        requestNextPermission()
    }

    private fun initParam() {
        val permissionGroupList: List<PermissionGroup>? =
            intent.getParcelableArrayListExtra(PermissionRequester.PARAM_KEY_PERMISSION_GROUP_LIST)
        permissionGroupList?.forEach {
            if (!it.permissionList.isNullOrEmpty()) {
                mPermissionGroupList.add(it)
            }
        }
    }

    /**
     * 初始化view，设置setContentView()
     */
    open fun initView(permissionGroupList: MutableList<PermissionGroup>) {
        //沉浸式状态栏
        StatusBarUtils.translucent(this)
    }

    open fun onRequestingPermission(permissionGroup: PermissionGroup) {}

    private fun requestNextPermission() {
        if (mPermissionGroupList.isEmpty()) {
            onRequestResult()
        } else {
            mRequestingPermissionGroup = mPermissionGroupList.removeAt(0)
            mRequestCode++
            onRequestingPermission(mRequestingPermissionGroup)
            requestDangerousPermissions()
        }
    }

    open fun onRequestResult() {
        val intent = Intent()
        PermissionResult.writeIntent(intent, mPermissionResult)
        setResult(RESULT_OK, intent)
        finish()
        overridePendingTransition(0, 0)
    }

    /**
     * 请求权限
     */
    private fun requestDangerousPermissions() {
        val hasNotPermissionList: MutableList<String> = mutableListOf()

        val permissionList = mRequestingPermissionGroup.permissionList
        permissionList.forEach {
            if (!PermissionUtils.hasPermission(this, it)) {
                hasNotPermissionList.add(it)
            }
        }

        if (hasNotPermissionList.isEmpty()) {
            onRequestDangerousPermissionsResult(permissionList, IntArray(permissionList.size) {
                PackageManager.PERMISSION_GRANTED
            })
        } else {
            ActivityCompat.requestPermissions(this, hasNotPermissionList.toTypedArray(), mRequestCode)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == mRequestCode) {
            onRequestDangerousPermissionsResult(permissions.toList(), grantResults)
            return
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun onRequestDangerousPermissionsResult(permissions: List<String>, intArray: IntArray) {
        mRequestingPermissionGroup.permissionList.forEach {
            val index = permissions.indexOf(it)
            val grantedResult: Int = if (index != -1) {
                intArray[index]
            } else {
                PackageManager.PERMISSION_GRANTED
            }
            mPermissionResult.addResult(it, grantedResult)
        }
        requestNextPermission()
    }
}