# RequestPermission
Android申请权限库，链式调用，权限结果回调，使用起来更优雅。


![jd-my](https://user-images.githubusercontent.com/13672396/177004404-35470f1e-a674-48d0-aa1a-d428b14e60b9.gif)


## 一、gradle依赖

````
implementation 'io.github.jinxiyang:requestpermission:0.0.2'
````


## 二、简单的使用介绍

a)、不使用中转activity，申请权限


```
//请求定位权限
PermissionRequester(this)
    .addPermissions(PermissionUtils.LOCATION_PERMISSIONS)
    .request {
        Toast.makeText(this@MainActivity, "申请定位权限：${it.granted()}", Toast.LENGTH_SHORT).show()
    }
```

b)、使用中转activity，申请权限


```
//请求定位权限
PermissionRequester(this)
    .addPermissions(PermissionUtils.LOCATION_PERMISSIONS)
    .requestGlobal {
        Toast.makeText(this@MainActivity, "申请定位权限：${it.granted()}", Toast.LENGTH_SHORT).show()
    }
```

c)、使用中转activity，申请权限，同时显示权限说明

```
//统一权限页面：仿京东，页面顶部显示权限提示，背景透明
private fun jdRequestPermission() {
    //定义申请权限组时的提示文字，更友好，不会被工信部点名胡乱获取用户隐私。
    //京东第一安装，打开首页相机时，会有这样的权限申请页，并在页面背景提示相关信息

    //相机权限对应的提示
    val cameraExtra = Bundle()
    cameraExtra.putString(RequestPermissionActivityJD.KEY_PERMISSION_TITLE, "相机权限使用说明")
    cameraExtra.putString(RequestPermissionActivityJD.KEY_PERMISSION_DESC, "京东正在向您获取“相机”权限，同意后，" +
            "你可以使用扫码服务，巴拉巴拉……")

    PermissionRequester(this)
        .addPermissionGroup(PermissionUtils.CAMERA_PERMISSIONS.toList(), cameraExtra)
        //设置自定义仿京东请求权限页面 RequestPermissionActivityJD，背景透明
        .requestGlobal(RequestPermissionActivityJD::class.java) {
            val camera = it.granted(PermissionUtils.CAMERA_PERMISSIONS)
            Toast.makeText(this@MainActivity, "申请相机权限：$camera", Toast.LENGTH_SHORT).show()
        }
}
```

## 三、博客地址：

**[https://juejin.cn/post/7115776072356102175/](https://juejin.cn/post/7115776072356102175/)**

