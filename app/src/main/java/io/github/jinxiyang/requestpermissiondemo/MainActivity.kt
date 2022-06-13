package io.github.jinxiyang.requestpermissiondemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity(), SimpleAdapter.OnClickItemListener{
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val list: MutableList<String> = ArrayList()
        list.add("默认")
        list.add("请求定位权限")

        val adapter = SimpleAdapter(list)
        recyclerView.adapter = adapter
        adapter.setOnClickItemListener(this)
    }

    override fun onClickItem(position: Int) {
        when (position) {
            0 -> {
//                navigateTo(LocationActivity::class.java)
            }
            1 -> {
//                requestLocationPermission()
            }
//            2 -> navigateToRecyclerViewHorizontal()
//            3 -> navigateToNestedScrollView()

//            "io.github.jinxiyang.requestpermission"
        }
    }
}