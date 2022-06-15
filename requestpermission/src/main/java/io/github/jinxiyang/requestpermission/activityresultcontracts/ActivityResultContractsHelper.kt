package io.github.jinxiyang.requestpermission.activityresultcontracts

import android.content.Intent
import androidx.fragment.app.FragmentManager

object ActivityResultContractsHelper {

    private const val FRAGMENT_TAG = "ActivityResultContractsFragment"

    @JvmStatic
    fun startActivityForResult(fm: FragmentManager, intent: Intent, onStartActivityForResultListener: OnStartActivityForResultListener) {
        val fragment = fm.findFragmentByTag(FRAGMENT_TAG)
        if (fragment is ActivityResultContractsFragment) {
            fragment.setOnStartActivityForResultListener(onStartActivityForResultListener)
            fragment.startActivityForResult(intent)
        } else {
            val activityResultFragment = ActivityResultContractsFragment.newInstance(intent)
            activityResultFragment.setOnStartActivityForResultListener(onStartActivityForResultListener)
            fm.beginTransaction()
                .add(activityResultFragment, FRAGMENT_TAG)
                .commitNowAllowingStateLoss()
        }
    }

    @JvmStatic
    fun requestMultiplePermissions(fm: FragmentManager, permissions: Array<String>, onStartActivityForResultListener: OnRequestMultiPermissionListener) {
        val fragment = fm.findFragmentByTag(FRAGMENT_TAG)
        if (fragment is ActivityResultContractsFragment) {
            fragment.setOnRequestMultiPermissionListener(onStartActivityForResultListener)
            fragment.requestMultiplePermissions(permissions)
        } else {
            val activityResultFragment = ActivityResultContractsFragment.newInstance(permissions = permissions)
            activityResultFragment.setOnRequestMultiPermissionListener(onStartActivityForResultListener)
            fm.beginTransaction()
                .add(activityResultFragment, FRAGMENT_TAG)
                .commitNowAllowingStateLoss()
        }
    }
}