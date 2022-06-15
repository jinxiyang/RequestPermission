package io.github.jinxiyang.requestpermission.activityresultcontracts

interface OnRequestMultiPermissionListener {
    fun onRequestMultiPermission(map: Map<String, Boolean>)
}