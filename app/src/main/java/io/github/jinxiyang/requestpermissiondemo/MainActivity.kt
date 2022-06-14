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
        list.add("应用信息")
        list.add("请求定位权限")

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
//            2 -> navigateToRecyclerViewHorizontal()
//            3 -> navigateToNestedScrollView()

//            "io.github.jinxiyang.requestpermission"
        }
    }

    private fun requestLocationPermission(){
        if (PermissionUtils.hasPermissions(this, PermissionUtils.LOCATION_PERMISSIONS)){
            Toast.makeText(this, "已经有定位权限了", Toast.LENGTH_SHORT).show()
            return
        }

        PermissionRequester()
            .setActivity(this)
            .addPermissions(PermissionUtils.LOCATION_PERMISSIONS)
            .requestWithHandledResult(object : PermissionRequester.OnRequestPermissionHandledResultListener{
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
}