package io.github.jinxiyang.requestpermission

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import io.github.jinxiyang.requestpermission.utils.PermissionUtils

open class PermissionRequester {

    private lateinit var mUiRequester: UiRequester

    private var mPermissionGroupList: ArrayList<PermissionGroup> = ArrayList()

    private var mRequestPermissionActivityClass: Class<*> = RequestPermissionActivity::class.java

    fun setActivity(activity: AppCompatActivity): PermissionRequester {
        mUiRequester = ActivityUiRequester(activity)
        return this
    }

    fun setFragment(fragment: Fragment): PermissionRequester {
        //避免没有绑定到activity
        fragment.requireActivity()
        mUiRequester = FragmentUiRequester(fragment)
        return this
    }

    /**
     * 设置自定义请求权限的activity
     */
    fun setCustomActivity(clazz: Class<*>): PermissionRequester {
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

    fun requestWithHandledResult(onRequestPermissionHandledResultListener: OnRequestPermissionHandledResultListener){
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

        val viewModel = ViewModelProvider(mUiRequester.getViewModelStoreOwner()).get(RequestResultViewModel::class.java)
        val liveDataTag = getLiveDataTag()
        val liveData = viewModel.createNewRequestResultLiveData(liveDataTag)
        liveData.observe(mUiRequester.getLifecycleOwner()) { result ->
            listener.onRequestPermissionResult(result.permissionList, result.grantedList)
            viewModel.removeLiveDataByTag(liveDataTag)
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
            }.toMutableList()
            liveData.postValue(RequestResult(permissionList, grantedList))
            return
        }

        //无权限时，开启权限统一请求页面
        val intent = Intent(activity, mRequestPermissionActivityClass)
        intent.putParcelableArrayListExtra(PARAM_KEY_PERMISSION_GROUP_LIST, mPermissionGroupList)
        intent.putExtra(PARAM_KEY_LIVE_DATA_TAG, liveDataTag)
        activity.startActivity(intent)
        //无动画打开权限页面
        activity.overridePendingTransition(0, 0)
    }

    private fun getLiveDataTag(): String {
        return mUiRequester.clazz.name + "_"+ mUiRequester.hashCode()
    }

    interface OnRequestPermissionResultListener{
        fun onRequestPermissionResult(permissionList: List<String>, grantResults: List<Int>)
    }

    interface OnRequestPermissionHandledResultListener{
        fun onRequestPermissionHandledResult(isGranted: Boolean)
    }

    abstract class UiRequester(val clazz: Class<*>){
        abstract fun getViewModelStoreOwner(): ViewModelStoreOwner
        abstract fun getLifecycleOwner(): LifecycleOwner
        abstract fun getActivity(): FragmentActivity
    }

    class ActivityUiRequester(val act: AppCompatActivity): UiRequester(act.javaClass) {

        override fun getViewModelStoreOwner(): ViewModelStoreOwner {
            return act
        }

        override fun getLifecycleOwner(): LifecycleOwner {
            return act
        }

        override fun getActivity(): FragmentActivity {
            return act
        }
    }

    class FragmentUiRequester(val fragment: Fragment): UiRequester(fragment.javaClass) {

        override fun getViewModelStoreOwner(): ViewModelStoreOwner {
            return fragment
        }

        override fun getLifecycleOwner(): LifecycleOwner {
            return fragment
        }

        override fun getActivity(): FragmentActivity {
            return fragment.requireActivity()
        }
    }

    companion object {
        const val PARAM_KEY_PERMISSION_GROUP_LIST = "permissionGroupList"
        const val PARAM_KEY_LIVE_DATA_TAG = "liveDataTag"
    }

}