package io.github.jinxiyang.requestpermission

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import io.github.jinxiyang.requestpermission.activityresultcontracts.ActivityResultContractsHelper
import io.github.jinxiyang.requestpermission.activityresultcontracts.OnRequestMultiPermissionListener
import io.github.jinxiyang.requestpermission.activityresultcontracts.OnStartActivityForResultListener
import io.github.jinxiyang.requestpermission.utils.PermissionUtils

/**
 * 权限申请者，封装了申请权限流程，链式调用，结果回调，使用起来更优雅。
 *
 * 优点：
 * 1、链式调用，结果回调listener，请求权限和结果回调代码写在一块儿，高内聚。
 *    不用在activity里处理权限结果回调方法，activity.onRequestPermissionsResult()
 *
 * 2、结果回调listener时，activity处于可见状态，即：activity.lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)
 *    这时如果显示对话框，DialogFragment.show()  没有这个问题了：java.lang.IllegalStateException: Can not perform this action after onSaveInstanceState
 *
 * 3、申请权限，系统的新方式是：
 *      a)、现在onCreate里注册launch
 *      val contract = ActivityResultContracts.RequestMultiplePermissions()
 *      val launcher = registerForActivityResult(contract){
 *          //权限结果回调
 *      }
 *
 *      b)、在需要申请权限的地方：  launcher.launch(permissionArray)
 *
 *      这种方式也是代码分开，没有做到高内聚，不能做到链式调用。
 *
 *
 * 如何使用：
 *    示例一：申请定位权限：
 *               PermissionRequester(this)
 *                  .addPermissions(PermissionUtils.LOCATION_PERMISSIONS)
 *                  .request {
 *                      Toast.makeText(this@MainActivity, "申请定位权限：${it.granted()}", Toast.LENGTH_SHORT).show()
 *                  }
 *
 *    示例二：使用统一中转页面，来申请权限：
 *              PermissionRequester(this)
 *                  .addPermissions(PermissionUtils.LOCATION_PERMISSIONS)
 *                  .addPermissions(PermissionUtils.CAMERA_PERMISSIONS)
 *                  .requestGlobal {
 *                      val location = it.granted(PermissionUtils.LOCATION_PERMISSIONS)
 *                      val camera = it.granted(PermissionUtils.CAMERA_PERMISSIONS)
 *                      Toast.makeText(this@MainActivity, "申请定位权限：$location   申请相机权限：$camera", Toast.LENGTH_SHORT).show()
 *                  }
 */
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
     * @param extra 额外数据，可以在统一权限页使用，例如申请权限时页面顶部显示权限说明，示例：UCRequestPermissionActivity
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

        //无权限时，开启公用的申请权限页面，申请权限
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