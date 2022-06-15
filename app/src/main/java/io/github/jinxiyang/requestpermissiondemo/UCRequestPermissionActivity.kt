package io.github.jinxiyang.requestpermissiondemo

import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import io.github.jinxiyang.requestpermission.PermissionGroup
import io.github.jinxiyang.requestpermission.GlobalRequestPermissionActivity
import io.github.jinxiyang.requestpermissiondemo.utils.StatusBarUtils

class UCRequestPermissionActivity : GlobalRequestPermissionActivity() {

    private lateinit var llContent: LinearLayout
    private lateinit var clContent: ConstraintLayout
    private lateinit var tvPermission: TextView
    private lateinit var tvPermissionDesc: TextView

    override fun initView(permissionGroupList: MutableList<PermissionGroup>) {
        super.initView(permissionGroupList)
        setContentView(R.layout.activity_uc_request_permission)
        llContent = findViewById(R.id.llContent)
        clContent = findViewById(R.id.clContent)
        tvPermission = findViewById(R.id.tvPermission)
        tvPermissionDesc = findViewById(R.id.tvPermissionDesc)

        llContent.setPadding(0, StatusBarUtils.getStatusBarHeight(this), 0, 0)

        showPermission()
    }

    override fun onRequestingPermission(permissionGroup: PermissionGroup) {
        val extra = permissionGroup.extra
        val permissionTitle = extra?.getString(KEY_PERMISSION_TITLE)
        val permissionDesc = extra?.getString(KEY_PERMISSION_DESC)
        showPermission(permissionTitle, permissionDesc)
    }

    private fun showPermission(permissionTitle: String? = null, permissionDesc: String? = null) {
        tvPermission.text = permissionTitle
        tvPermissionDesc.text = permissionDesc

        clContent.visibility = if (permissionTitle.isNullOrEmpty() && permissionDesc.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }


    companion object {
        const val KEY_PERMISSION_TITLE = "permission_title"
        const val KEY_PERMISSION_DESC = "permission_desc"
    }
}