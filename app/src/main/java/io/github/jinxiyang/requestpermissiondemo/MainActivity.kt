package io.github.jinxiyang.requestpermissiondemo

import android.content.Intent
import android.content.pm.PackageManager
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

        list.add("仿UC，请求多个权限，页面顶部显示权限提示")

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
            1 -> {
                requestLocationPermission()
            }
            2 -> {
                requestMultiPermissions()
            }

            3 -> {
                ucRequestPermission()
            }
        }
    }


    private fun requestLocationPermission(){
        if (PermissionUtils.hasPermissions(this, PermissionUtils.LOCATION_PERMISSIONS)){
            Toast.makeText(this, "已经有定位权限了", Toast.LENGTH_SHORT).show()
            return
        }

        PermissionRequester()
            .setUiRequester(this)
            .addPermissions(PermissionUtils.LOCATION_PERMISSIONS)
            .request(object : PermissionRequester.OnRequestPermissionHandledResultListener{
                override fun onRequestPermissionHandledResult(isGranted: Boolean) {
                    val success = if (isGranted) {
                        "成功"
                    } else {
                        "失败"
                    }
                    Toast.makeText(this@MainActivity, "申请定位权限：$success", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun requestMultiPermissions() {
        PermissionRequester()
            .setUiRequester(this)
            .addPermissions(PermissionUtils.LOCATION_PERMISSIONS)
            .addPermissions(PermissionUtils.CAMERA_PERMISSIONS)
            .request(object : PermissionRequester.OnRequestPermissionResultListener{
                override fun onRequestPermissionResult(permissionList: List<String>, grantResults: List<Int>) {
                    var location = true
                    var camera = true

                    permissionList.forEachIndexed { index, s ->
                        val permissionGranted = grantResults[index] == PackageManager.PERMISSION_GRANTED
                        if (s in PermissionUtils.LOCATION_PERMISSIONS) {
                            location = location && permissionGranted
                        } else if (s in PermissionUtils.CAMERA_PERMISSIONS) {
                            camera = camera && permissionGranted
                        }
                    }

                    Toast.makeText(this@MainActivity, "申请定位权限：$location   申请相机权限：$camera", Toast.LENGTH_SHORT).show()
                }
            })
    }


    private fun ucRequestPermission() {
        val storageExtra = Bundle()
        storageExtra.putString(UCRequestPermissionActivity.KEY_PERMISSION_TITLE, "存储权限使用说明")
        storageExtra.putString(UCRequestPermissionActivity.KEY_PERMISSION_DESC, "UC浏览器正在向您获取“存储”权限，同意后，" +
                "以便向您提供契合需求的产品服务等")

        val cameraExtra = Bundle()
        cameraExtra.putString(UCRequestPermissionActivity.KEY_PERMISSION_TITLE, "相机权限使用说明")
        cameraExtra.putString(UCRequestPermissionActivity.KEY_PERMISSION_DESC, "UC浏览器正在向您获取“相机”权限，同意后，" +
                "你可以使用扫码服务，巴拉巴拉……")

        PermissionRequester()
            .setUiRequester(this)
            .setCustomRequestPermissionActivity(UCRequestPermissionActivity::class.java)
            .addPermissionGroup(PermissionUtils.STORAGE_PERMISSIONS.toList(), storageExtra)
            .addPermissionGroup(PermissionUtils.CAMERA_PERMISSIONS.toList(), cameraExtra)
            .request(object : PermissionRequester.OnRequestPermissionResultListener{
                override fun onRequestPermissionResult(permissionList: List<String>, grantResults: List<Int>) {
                    var storage = true
                    var camera = true

                    permissionList.forEachIndexed { index, s ->
                        val permissionGranted = grantResults[index] == PackageManager.PERMISSION_GRANTED
                        if (s in PermissionUtils.STORAGE_PERMISSIONS) {
                            storage = storage && permissionGranted
                        } else if (s in PermissionUtils.CAMERA_PERMISSIONS) {
                            camera = camera && permissionGranted
                        }
                    }

                    Toast.makeText(this@MainActivity, "申请存储权限：$storage   申请相机权限：$camera", Toast.LENGTH_SHORT).show()
                }
            })
    }
}