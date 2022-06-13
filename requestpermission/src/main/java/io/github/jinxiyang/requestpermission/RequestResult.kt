package io.github.jinxiyang.requestpermission

class RequestResult(
    val permissionList: MutableList<String> = mutableListOf(),
    val grantedList: MutableList<Int> = mutableListOf()
)