package io.github.jinxiyang.requestpermission.activityresult

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData

class ActivityResultFragment : Fragment() {

    private lateinit var launcher: ActivityResultLauncher<Intent>

    private val intentLiveData = MutableLiveData<Intent>()

    private var onActivityResultListener: OnActivityResultListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val contract = StartActivityForResult()
        launcher = registerForActivityResult(contract) { result: ActivityResult ->
            onActivityResultListener?.onActivityResult(result.resultCode, result.data)
        }

        val args = arguments
        val intent = arguments?.getParcelable<Intent>("intent")
        if (intent != null) {
            args?.putParcelable("intent", null)
            launch(intent)
        }

        intentLiveData.observe(this) {
            launcher.launch(it)
        }
    }

    fun launch(intent: Intent?) {
        intentLiveData.postValue(intent)
    }

    fun setOnActivityResultListener(onActivityResultListener: OnActivityResultListener?) {
        this.onActivityResultListener = onActivityResultListener
    }

    companion object {

        fun newInstance(intent: Intent): ActivityResultFragment {
            val args = Bundle()
            args.putParcelable("intent", intent)
            val fragment = ActivityResultFragment()
            fragment.arguments = args
            return fragment
        }
    }
}