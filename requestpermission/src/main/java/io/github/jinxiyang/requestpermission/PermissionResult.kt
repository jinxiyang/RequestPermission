package io.github.jinxiyang.requestpermission

import android.content.Intent
import android.content.pm.PackageManager

/**
 * 权限申请的结果
 */
class PermissionResult {

    private val permissionList: MutableList<String>
    private val grantedResultList: MutableList<Int>

    constructor() {
        permissionList = ArrayList()
        grantedResultList = ArrayList()
    }

    constructor(permissionList: MutableList<String>, grantedResultList: MutableList<Int>) {
        this.permissionList = permissionList
        this.grantedResultList = grantedResultList
    }

    /**
     * 是否已经全部授权
     */
    fun granted(): Boolean {
        grantedResultList.forEach {
            if (it != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }

    /**
     * 某个权限是否已经授权
     */
    fun granted(permission: String): Boolean {
        val index = permissionList.indexOf(permission)
        if (index >= 0) {
            return grantedResultList[index] == PackageManager.PERMISSION_GRANTED
        }
        return true
    }

    /**
     * 某些权限是否已经授权
     */
    fun granted(permissions: List<String>): Boolean {
        var granted = true
        permissionList.forEachIndexed { index, s ->
            if (s in permissions) {
                granted = granted && grantedResultList[index] == PackageManager.PERMISSION_GRANTED
            }
        }
        return granted
    }

    /**
     * 某组权限是否已经授权
     */
    fun granted(permissions: Array<String>): Boolean {
        var granted = true
        permissionList.forEachIndexed { index, s ->
            if (s in permissions) {
                granted = granted && grantedResultList[index] == PackageManager.PERMISSION_GRANTED
            }
        }
        return granted
    }

    /**
     * 没有授权的权限
     */
    fun notGrantedArray(): Array<String>{
        val notGrantedList: MutableList<String> = mutableListOf()
        grantedResultList.forEachIndexed { index, i ->
            if (i != PackageManager.PERMISSION_GRANTED){
                notGrantedList.add(permissionList[index])
            }
        }
        return notGrantedList.toTypedArray()
    }

    /**
     * 添加一条请求权限结果
     */
    fun addResult(permission: String, grantedResult: Int) {
        permissionList.add(permission)
        grantedResultList.add(grantedResult)
    }

    /**
     * 设置请求权限结果
     */
    fun setResult(resultMap: Map<String, Boolean>){
        permissionList.forEachIndexed { index, s ->
            val granted = resultMap[s]
            //如果结果中没有此项权限，不重新设置结果
            if (granted != null) {
                grantedResultList[index] = if (granted) {
                    PackageManager.PERMISSION_GRANTED
                } else {
                    PackageManager.PERMISSION_DENIED
                }
            }
        }
    }

    companion object {
        private const val RESULT_KEY_PERMISSION_LIST = "permissionList"
        private const val RESULT_KEY_GRANTED_LIST = "grantedList"

        fun writeIntent(intent: Intent, permissionResult: PermissionResult){
            intent.putStringArrayListExtra(RESULT_KEY_PERMISSION_LIST, permissionResult.permissionList as ArrayList<String>)
            intent.putIntegerArrayListExtra(RESULT_KEY_GRANTED_LIST, permissionResult.grantedResultList as ArrayList<Int>)
        }

        fun readIntent(intent: Intent?, defaultResult: PermissionResult): PermissionResult{
            val permissionList: MutableList<String>? = intent?.getStringArrayListExtra(RESULT_KEY_PERMISSION_LIST)
            val grantedResultList: MutableList<Int>? = intent?.getIntegerArrayListExtra(RESULT_KEY_GRANTED_LIST)
            return if (permissionList != null && grantedResultList != null) {
                PermissionResult(permissionList, grantedResultList)
            } else {
                defaultResult
            }
        }
    }
}