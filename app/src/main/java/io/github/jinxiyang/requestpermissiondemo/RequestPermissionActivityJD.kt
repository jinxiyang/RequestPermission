package io.github.jinxiyang.requestpermissiondemo

import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import io.github.jinxiyang.requestpermission.PermissionGroup
import io.github.jinxiyang.requestpermission.GlobalRequestPermissionActivity
import io.github.jinxiyang.requestpermissiondemo.utils.StatusBarUtils

/**
 * 统一权限页面：仿JD，页面顶部显示权限提示，背景透明，配合
 *
 * 京东第一安装，打开首页相机时，会有这样的权限申请页，并在页面背景提示相关信息
 *
 * RequestPermissionActivityJD和RequestPermissionActivityUC代码一样，只是布局文件不一样
 */
class RequestPermissionActivityJD : GlobalRequestPermissionActivity() {

    private lateinit var llContent: LinearLayout
    private lateinit var clContent: ConstraintLayout
    private lateinit var tvPermission: TextView
    private lateinit var tvPermissionDesc: TextView

    override fun initView(permissionGroupList: MutableList<PermissionGroup>) {
        super.initView(permissionGroupList)
        setContentView(R.layout.activity_request_permission_jd)
        llContent = findViewById(R.id.llContent)
        clContent = findViewById(R.id.clContent)
        tvPermission = findViewById(R.id.tvPermission)
        tvPermissionDesc = findViewById(R.id.tvPermissionDesc)

        llContent.setPadding(0, StatusBarUtils.getStatusBarHeight(this), 0, 0)

        showPermission()
    }

    override fun onRequestingPermission(permissionGroup: PermissionGroup) {
        //当申请一组权限时，会回调此方法，可以在页面上显示权限提示信息，比如：为什么要申请此权限等等
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