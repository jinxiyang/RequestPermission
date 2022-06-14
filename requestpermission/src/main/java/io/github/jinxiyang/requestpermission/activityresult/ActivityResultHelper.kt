package io.github.jinxiyang.requestpermission.activityresult

import android.content.Intent
import androidx.fragment.app.FragmentManager

object ActivityResultHelper {

    private const val FRAGMENT_TAG = "StartActivityForResultFragment"

    @JvmStatic
    fun startActivityForResult(fm: FragmentManager, intent: Intent, onActivityResultListener: OnActivityResultListener) {
        val fragment = fm.findFragmentByTag(FRAGMENT_TAG)
        if (fragment is ActivityResultFragment) {
            fragment.setOnActivityResultListener(onActivityResultListener)
            fragment.launch(intent)
        } else {
            val activityResultFragment = ActivityResultFragment.newInstance(intent)
            activityResultFragment.setOnActivityResultListener(onActivityResultListener)
            fm.beginTransaction()
                .add(activityResultFragment, FRAGMENT_TAG)
                .commitNowAllowingStateLoss()
        }
    }
}