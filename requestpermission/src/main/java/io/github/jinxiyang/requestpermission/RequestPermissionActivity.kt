package io.github.jinxiyang.requestpermission

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import io.github.jinxiyang.requestpermission.utils.PermissionUtils
import io.github.jinxiyang.requestpermission.utils.StatusBarUtils

open class RequestPermissionActivity : AppCompatActivity() {

    private val mPermissionGroupList: MutableList<PermissionGroup> = mutableListOf()
    private val mRequestResult: RequestResult = RequestResult()

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

    private fun requestNextPermission() {
        if (mPermissionGroupList.isEmpty()) {
            onRequestDangerousPermissions()
        } else {
            mRequestingPermissionGroup = mPermissionGroupList.removeAt(0)
            mRequestCode++
            onRequestingPermission(mRequestingPermissionGroup, mRequestCode)
        }
    }

    open fun onRequestingPermission(permissionGroup: PermissionGroup, requestCode: Int) {
        requestDangerousPermissions(permissionGroup.permissionList, requestCode)
    }

    /**
     * 请求权限
     */
    open fun requestDangerousPermissions(permissions: List<String>, requestCode: Int) {
        val hasNotPermissionList: MutableList<String> = mutableListOf()

        if (PermissionUtils.hasPermissions(this, permissions, hasNotPermissionList)) {
            onRequestResult(permissions, IntArray(permissions.size) {
                PackageManager.PERMISSION_GRANTED
            })
        } else {
            ActivityCompat.requestPermissions(this, hasNotPermissionList.toTypedArray(), requestCode)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == mRequestCode) {
            onRequestResult(permissions.toList(), grantResults)
            return
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun onRequestResult(permissions: List<String>, intArray: IntArray) {
        mRequestingPermissionGroup.permissionList?.forEach {
            val index = permissions.indexOf(it)
            val granted: Int = if (index != -1) {
                intArray[index]
            } else {
                PackageManager.PERMISSION_GRANTED
            }
            mRequestResult.permissionList.add(it)
            mRequestResult.grantedList.add(granted)
        }
        requestNextPermission()
    }

    open fun onRequestDangerousPermissions() {
        val viewModel = ViewModelProvider(this).get(RequestResultViewModel::class.java)
        val liveDataTag = intent.getStringExtra(PermissionRequester.PARAM_KEY_LIVE_DATA_TAG) ?: ""
        viewModel.getRequestResultLiveData(liveDataTag)?.postValue(mRequestResult)
        finish()
        overridePendingTransition(0, 0)
    }
}