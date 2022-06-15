package io.github.jinxiyang.requestpermission.activityresultcontracts

import android.content.Intent

interface OnStartActivityForResultListener {
    fun onActivityResult(resultCode: Int, intent: Intent?)
}