package io.github.jinxiyang.requestpermissiondemo

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.github.jinxiyang.requestpermission.PermissionRequester
import io.github.jinxiyang.requestpermission.utils.PermissionUtils

class MainActivity : AppCompatActivity(), SimpleAdapter.OnClickItemListener{
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val list: MutableList<String> = ArrayList()
        list.add("重置权限     (1、点击存储  2、点击删除数据)")
        list.add("请求定位权限")
        list.add("请求多个权限：定位、相机")


        list.add("统一权限页面：请求定位权限")
        list.add("统一权限页面：请求多个权限：定位、相机")
        list.add("统一权限页面：仿UC，页面顶部显示权限提示")

        val adapter = SimpleAdapter(list)
        recyclerView.adapter = adapter
        adapter.setOnClickItemListener(this)
    }

    override fun onClickItem(position: Int) {
        when (position) {
            0 -> {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.data = Uri.parse("package:$packageName")
                startActivity(intent)
            }

            1 -> requestLocationPermission()
            2 -> requestMultiPermissions()

            3 -> requestLocationPermission2()
            4 -> requestMultiPermissions2()
            5 -> ucRequestPermission()
        }
    }

    //请求定位权限
    private fun requestLocationPermission(){
        if (PermissionUtils.hasPermissions(this, PermissionUtils.LOCATION_PERMISSIONS)){
            Toast.makeText(this, "已经有定位权限了", Toast.LENGTH_SHORT).show()
            return
        }

        PermissionRequester(this)
            .addPermissions(PermissionUtils.LOCATION_PERMISSIONS)
            .request {
                Toast.makeText(this@MainActivity, "申请定位权限：${it.granted()}", Toast.LENGTH_SHORT).show()
            }
    }

    //请求多个权限：定位、相机
    private fun requestMultiPermissions() {
        PermissionRequester(this)
            .addPermissions(PermissionUtils.LOCATION_PERMISSIONS)
            .addPermissions(PermissionUtils.CAMERA_PERMISSIONS)
            .request {
                val location = it.granted(PermissionUtils.LOCATION_PERMISSIONS)
                val camera = it.granted(PermissionUtils.CAMERA_PERMISSIONS)
                Toast.makeText(this@MainActivity, "申请定位权限：$location   申请相机权限：$camera", Toast.LENGTH_SHORT).show()
            }
    }

    //统一权限页面：请求定位权限
    private fun requestLocationPermission2(){
        if (PermissionUtils.hasPermissions(this, PermissionUtils.LOCATION_PERMISSIONS)){
            Toast.makeText(this, "已经有定位权限了", Toast.LENGTH_SHORT).show()
            return
        }

        PermissionRequester(this)
            .addPermissions(PermissionUtils.LOCATION_PERMISSIONS)
            .requestGlobal {
                Toast.makeText(this@MainActivity, "申请定位权限：${it.granted()}", Toast.LENGTH_SHORT).show()
            }
    }

    //统一权限页面：请求多个权限：定位、相机
    private fun requestMultiPermissions2() {
        PermissionRequester(this)
            .addPermissions(PermissionUtils.LOCATION_PERMISSIONS)
            .addPermissions(PermissionUtils.CAMERA_PERMISSIONS)
            .requestGlobal {
                val location = it.granted(PermissionUtils.LOCATION_PERMISSIONS)
                val camera = it.granted(PermissionUtils.CAMERA_PERMISSIONS)
                Toast.makeText(this@MainActivity, "申请定位权限：$location   申请相机权限：$camera", Toast.LENGTH_SHORT).show()
            }
    }

    //统一权限页面：仿UC，页面顶部显示权限提示
    private fun ucRequestPermission() {
        //定义申请权限组时的提示文字，更友好，不会被工信部点名胡乱获取用户隐私。
        //UC浏览器第一安装时，会有这样的权限申请页，并在页面背景提示相关信息

        //存储权限对应的提示
        val storageExtra = Bundle()
        storageExtra.putString(UCRequestPermissionActivity.KEY_PERMISSION_TITLE, "存储权限使用说明")
        storageExtra.putString(UCRequestPermissionActivity.KEY_PERMISSION_DESC, "UC浏览器正在向您获取“存储”权限，同意后，" +
                "以便向您提供契合需求的产品服务等")

        //相机权限对应的提示
        val cameraExtra = Bundle()
        cameraExtra.putString(UCRequestPermissionActivity.KEY_PERMISSION_TITLE, "相机权限使用说明")
        cameraExtra.putString(UCRequestPermissionActivity.KEY_PERMISSION_DESC, "UC浏览器正在向您获取“相机”权限，同意后，" +
                "你可以使用扫码服务，巴拉巴拉……")

        PermissionRequester(this)
            .addPermissionGroup(PermissionUtils.STORAGE_PERMISSIONS.toList(), storageExtra)
            .addPermissionGroup(PermissionUtils.CAMERA_PERMISSIONS.toList(), cameraExtra)
            //设置自定义UC请求权限页面 UCRequestPermissionActivity
            .requestGlobal(UCRequestPermissionActivity::class.java) {
                val storage = it.granted(PermissionUtils.STORAGE_PERMISSIONS)
                val camera = it.granted(PermissionUtils.CAMERA_PERMISSIONS)
                Toast.makeText(this@MainActivity, "申请存储权限：$storage   申请相机权限：$camera", Toast.LENGTH_SHORT).show()
            }
    }
}