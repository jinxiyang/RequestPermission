package io.github.jinxiyang.requestpermission

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import io.github.jinxiyang.requestpermission.activityresultcontracts.ActivityResultContractsHelper
import io.github.jinxiyang.requestpermission.activityresultcontracts.OnRequestMultiPermissionListener
import io.github.jinxiyang.requestpermission.activityresultcontracts.OnStartActivityForResultListener
import io.github.jinxiyang.requestpermission.utils.PermissionUtils

class PermissionRequester(val activity: FragmentActivity) {

    private val mPermissionGroupList: ArrayList<PermissionGroup> = ArrayList()

    constructor(fragment: Fragment): this(fragment.requireActivity())

    /**
     * 添加一组需要申请
     *
     * @param permissionGroup 权限组，如：存储权限：{@link PermissionUtils.STORAGE_PERMISSIONS}
     */
    fun addPermissionGroup(permissionGroup: PermissionGroup): PermissionRequester {
        mPermissionGroupList.add(permissionGroup)
        return this
    }

    /**
     * 添加需要申请的权限组
     *
     * @param permissionList 一组权限列表，如：存储权限：{@link PermissionUtils.STORAGE_PERMISSIONS}
     * @param extra 额外数据，可以再统一权限页使用，例如申请权限时页面顶部显示权限说明，示例：UCRequestPermissionActivity
     */
    fun addPermissionGroup(permissionList: List<String>, extra: Bundle? = null): PermissionRequester {
        return addPermissionGroup(PermissionGroup(permissionList, extra))
    }


    /**
     * 添加需要申请的权限
     */
    fun addPermission(permission: String): PermissionRequester {
        val permissions = mutableListOf<String>()
        permissions.add(permission)
        return addPermissionGroup(permissions)
    }

    /**
     * 添加需要申请的权限
     */
    fun addPermissions(permissions: Array<String>): PermissionRequester {
        return addPermissionGroup(permissions.toList())
    }

    /**
     * 请求权限
     * @param listener 权限结果回调listener
     */
    fun request(listener: (PermissionResult) -> Unit) {
        request(object : OnRequestPermissionResultListener{
            override fun onResult(result: PermissionResult) {
                listener(result)
            }
        })
    }

    /**
     * 请求权限
     * @param listener 权限结果回调listener
     */
    fun request(listener: OnRequestPermissionResultListener){
        //查询是否有权限，如果有权限直接回调listener
        val permissionResult = PermissionUtils.checkPermissions(activity, mPermissionGroupList)
        if (permissionResult.granted()) {
            listener.onResult(permissionResult)
            return
        }

        //没有授权的权限
        val notGrantedArray = permissionResult.notGrantedArray()
        val fm = activity.supportFragmentManager
        ActivityResultContractsHelper.requestMultiplePermissions(fm, notGrantedArray, object : OnRequestMultiPermissionListener{
            override fun onRequestMultiPermission(map: Map<String, Boolean>) {
                permissionResult.setResult(map)
                listener.onResult(permissionResult)
            }
        })
    }

    /**
     * 请求权限，使用公用的请求权限中转页面（默认：GlobalRequestPermissionActivity）
     *
     * @param listener 权限结果回调listener
     */
    fun requestGlobal(listener: (PermissionResult) -> Unit) {
        requestGlobal(GlobalRequestPermissionActivity::class.java, listener)
    }

    /**
     * 请求权限，使用公用的请求权限中转页面（默认：GlobalRequestPermissionActivity）
     *
     * @param listener 权限结果回调listener
     */
    fun requestGlobal(listener: OnRequestPermissionResultListener){
        requestGlobal(GlobalRequestPermissionActivity::class.java, listener)
    }

    /**
     * 请求权限，使用公用的请求权限中转页面
     *
     * @param requestPermissionActivityClass 公用的请求权限中转页面
     * @param listener 权限结果回调listener
     */
    fun requestGlobal(requestPermissionActivityClass: Class<*>, listener: (PermissionResult) -> Unit){
        val l = object : OnRequestPermissionResultListener {
            override fun onResult(result: PermissionResult) {
                listener(result)
            }
        }
        requestGlobal(requestPermissionActivityClass, l)
    }

    /**
     * 请求权限，使用公用的请求权限中转页面
     *
     * @param requestPermissionActivityClass 公用的请求权限中转页面
     * @param listener 权限结果回调listener
     */
    fun requestGlobal(requestPermissionActivityClass: Class<*>, listener: OnRequestPermissionResultListener){
        //查询是否有权限，如果有权限直接回调listener
        val permissionResult = PermissionUtils.checkPermissions(activity, mPermissionGroupList)
        if (permissionResult.granted()) {
            listener.onResult(permissionResult)
            return
        }

        //无权限时，开启权限统一请求页面
        val intent = Intent(activity, requestPermissionActivityClass)
        intent.putParcelableArrayListExtra(PARAM_KEY_PERMISSION_GROUP_LIST, mPermissionGroupList)

        ActivityResultContractsHelper.startActivityForResult(activity.supportFragmentManager, intent, object : OnStartActivityForResultListener {

            override fun onActivityResult(resultCode: Int, intent: Intent?) {
                listener.onResult(PermissionResult.readIntent(intent, permissionResult))
            }
        })
    }

    companion object {
        const val PARAM_KEY_PERMISSION_GROUP_LIST = "permissionGroupList"
    }
}