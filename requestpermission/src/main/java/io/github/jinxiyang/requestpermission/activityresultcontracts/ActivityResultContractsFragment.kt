package io.github.jinxiyang.requestpermission.activityresultcontracts

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.core.app.ActivityOptionsCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData

class ActivityResultContractsFragment : Fragment() {

    private lateinit var mStartActivityForResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var mRequestMultiplePermissionsLauncher: ActivityResultLauncher<Array<String>>

    private val mStartActivityForResultLiveData = MutableLiveData<Intent>()
    private val mRequestMultiplePermissionsLiveData = MutableLiveData<Array<String>>()

    private var mOnStartActivityForResultListener: OnStartActivityForResultListener? = null
    private var mOnRequestMultiPermissionListener: OnRequestMultiPermissionListener? = null

    private val activityOptionsCompat: ActivityOptionsCompat by lazy {
        //页面转场无动画
        ActivityOptionsCompat.makeCustomAnimation(requireContext(), 0, 0)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val startActivityForResult = StartActivityForResult()
        mStartActivityForResultLauncher = registerForActivityResult(startActivityForResult) {
            mOnStartActivityForResultListener?.onActivityResult(it.resultCode, it.data)
        }

        val requestMultiplePermissions = ActivityResultContracts.RequestMultiplePermissions()
        mRequestMultiplePermissionsLauncher = registerForActivityResult(requestMultiplePermissions) {
            mOnRequestMultiPermissionListener?.onRequestMultiPermission(it)
        }

        val args = arguments
        val intent = args?.getParcelable<Intent>("intent")
        if (intent != null) {
            args.putParcelable("intent", null)
            startActivityForResult(intent)
        }
        mStartActivityForResultLiveData.observe(this) {
            mStartActivityForResultLauncher.launch(it, activityOptionsCompat)
        }

        val permissions: Array<String>? = args?.getStringArray("permissions")
        if (permissions != null) {
            args.putStringArray("permissions", null)
            requestMultiplePermissions(permissions)
        }
        mRequestMultiplePermissionsLiveData.observe(this) {
            mRequestMultiplePermissionsLauncher.launch(it)
        }
    }

    fun startActivityForResult(intent: Intent) {
        mStartActivityForResultLiveData.postValue(intent)
    }

    fun requestMultiplePermissions(permissions: Array<String>) {
        mRequestMultiplePermissionsLiveData.postValue(permissions)
    }

    fun setOnStartActivityForResultListener(onStartActivityForResultListener: OnStartActivityForResultListener?) {
        this.mOnStartActivityForResultListener = onStartActivityForResultListener
    }

    fun setOnRequestMultiPermissionListener(onRequestMultiPermissionListener: OnRequestMultiPermissionListener?) {
        this.mOnRequestMultiPermissionListener = onRequestMultiPermissionListener
    }

    companion object {

        fun newInstance(intent: Intent? = null, permissions: Array<String>? = null): ActivityResultContractsFragment {
            val args = Bundle()
            args.putParcelable("intent", intent)
            args.putStringArray("permissions", permissions)
            val fragment = ActivityResultContractsFragment()
            fragment.arguments = args
            return fragment
        }
    }
}