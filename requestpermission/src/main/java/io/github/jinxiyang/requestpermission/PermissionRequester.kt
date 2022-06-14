package io.github.jinxiyang.requestpermission

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import io.github.jinxiyang.requestpermission.activityresult.ActivityResultHelper
import io.github.jinxiyang.requestpermission.activityresult.OnActivityResultListener
import io.github.jinxiyang.requestpermission.utils.PermissionUtils

class PermissionRequester {

    private lateinit var mUiRequester: UiRequester

    private var mPermissionGroupList: ArrayList<PermissionGroup> = ArrayList()

    private var mRequestPermissionActivityClass: Class<*> = RequestPermissionActivity::class.java

    fun setUiRequester(activity: AppCompatActivity): PermissionRequester {
        mUiRequester = ActivityUiRequester(activity)
        return this
    }

    fun setUiRequester(fragment: Fragment): PermissionRequester {
        //避免没有绑定到activity
        fragment.requireActivity()
        mUiRequester = FragmentUiRequester(fragment)
        return this
    }

    /**
     * 设置自定义请求权限的activity
     */
    fun setCustomRequestPermissionActivity(clazz: Class<*>): PermissionRequester {
        mRequestPermissionActivityClass = clazz
        return this
    }

    fun addPermissionGroup(permissionGroup: PermissionGroup): PermissionRequester {
        mPermissionGroupList.add(permissionGroup)
        return this
    }

    fun addPermissionGroup(permissionList: List<String>, extra: Bundle? = null): PermissionRequester {
        return addPermissionGroup(PermissionGroup(permissionList, extra))
    }

    fun addPermission(permission: String): PermissionRequester {
        val permissions = mutableListOf<String>()
        permissions.add(permission)
        return addPermissionGroup(permissions)
    }

    fun addPermissions(permissions: Array<String>): PermissionRequester {
        return addPermissionGroup(permissions.toList())
    }

    fun request(onRequestPermissionResultListener: OnRequestPermissionResultListener){
        doRequest(onRequestPermissionResultListener)
    }

    fun request(onRequestPermissionHandledResultListener: OnRequestPermissionHandledResultListener){
        doRequest(object : OnRequestPermissionResultListener {
            override fun onRequestPermissionResult(permissionList: List<String>, grantResults: List<Int>) {
                //是否已经全部授权
                var isGranted = true
                grantResults.forEach { grant ->
                    if (grant != PackageManager.PERMISSION_GRANTED) {
                        isGranted = false
                    }
                }
                onRequestPermissionHandledResultListener.onRequestPermissionHandledResult(isGranted)
            }
        })
    }

    private fun doRequest(listener: OnRequestPermissionResultListener){
        if (!::mUiRequester.isInitialized) {
            throw RuntimeException("activity or fragment cannot be null")
        }

        val activity = mUiRequester.getActivity()

        //查询是否有权限，如果有权限直接回调listener
        val permissionList = mutableListOf<String>()
        mPermissionGroupList.forEach {
            permissionList.addAll(it.permissionList)
        }
        if (PermissionUtils.hasPermissions(activity, permissionList)) {
            val grantedList = IntArray(permissionList.size) {
                PackageManager.PERMISSION_GRANTED
            }.toList()
            listener.onRequestPermissionResult(permissionList, grantedList)
            return
        }

        //无权限时，开启权限统一请求页面
        val intent = Intent(activity, mRequestPermissionActivityClass)
        intent.putParcelableArrayListExtra(PARAM_KEY_PERMISSION_GROUP_LIST, mPermissionGroupList)

        ActivityResultHelper.startActivityForResult(activity.supportFragmentManager, intent, object : OnActivityResultListener {

            override fun onActivityResult(resultCode: Int, intent: Intent?) {
                val resultPermissionList = intent?.getStringArrayListExtra(RequestPermissionActivity.RESULT_KEY_PERMISSION_LIST)
                val resultGrantedList = intent?.getIntegerArrayListExtra(RequestPermissionActivity.RESULT_KEY_GRANTED_LIST)
                if (resultPermissionList != null && resultGrantedList != null) {
                    listener.onRequestPermissionResult(resultPermissionList, resultGrantedList)
                } else {
                    val grantedList = IntArray(permissionList.size) {
                        PackageManager.PERMISSION_DENIED
                    }.toList()
                    listener.onRequestPermissionResult(permissionList, grantedList)
                }
            }
        })
    }

    interface OnRequestPermissionResultListener{
        fun onRequestPermissionResult(permissionList: List<String>, grantResults: List<Int>)
    }

    interface OnRequestPermissionHandledResultListener {
        fun onRequestPermissionHandledResult(isGranted: Boolean)
    }

    private abstract class UiRequester{
        abstract fun getActivity(): FragmentActivity
    }

    private class ActivityUiRequester(val act: AppCompatActivity): UiRequester() {

        override fun getActivity(): FragmentActivity {
            return act
        }
    }

    private class FragmentUiRequester(val fragment: Fragment): UiRequester() {

        override fun getActivity(): FragmentActivity {
            return fragment.requireActivity()
        }
    }

    companion object {
        const val PARAM_KEY_PERMISSION_GROUP_LIST = "permissionGroupList"
    }

}