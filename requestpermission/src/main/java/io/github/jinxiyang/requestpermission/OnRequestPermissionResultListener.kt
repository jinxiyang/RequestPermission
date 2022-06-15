package io.github.jinxiyang.requestpermission

/**
 * 申请权限结果回调listener
 */
interface OnRequestPermissionResultListener {
    /**
     * 申请权限结果回调此方法
     */
    fun onResult(result: PermissionResult)
}