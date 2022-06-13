package io.github.jinxiyang.requestpermission

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData

class RequestResultViewModel(application: Application) : AndroidViewModel(application) {

    private val mResultMap: MutableMap<String, MutableLiveData<RequestResult>> by lazy {
        mutableMapOf()
    }

    fun getRequestResultLiveData(tag: String): MutableLiveData<RequestResult>?{
        return mResultMap[tag]
    }

    fun createNewRequestResultLiveData(tag: String): MutableLiveData<RequestResult> {
        val liveData = MutableLiveData<RequestResult>()
        mResultMap[tag] = liveData
        return liveData
    }

    fun removeLiveDataByTag(tag: String) {
        mResultMap.remove(tag)
    }
}