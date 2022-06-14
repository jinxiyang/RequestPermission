package io.github.jinxiyang.requestpermission.activityresult

import android.content.Intent

interface OnActivityResultListener {
    fun onActivityResult(resultCode: Int, intent: Intent?)
}