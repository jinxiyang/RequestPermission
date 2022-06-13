package io.github.jinxiyang.requestpermission;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class PermissionGroup implements Parcelable {

    private List<String> permissionList;
    private Bundle extra;

    public PermissionGroup() {
    }

    public PermissionGroup(List<String> permissionList, Bundle extra) {
        this.permissionList = permissionList;
        this.extra = extra;
    }

    protected PermissionGroup(Parcel in) {
        permissionList = in.createStringArrayList();
        extra = in.readBundle();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringList(permissionList);
        dest.writeBundle(extra);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<PermissionGroup> CREATOR = new Creator<PermissionGroup>() {
        @Override
        public PermissionGroup createFromParcel(Parcel in) {
            return new PermissionGroup(in);
        }

        @Override
        public PermissionGroup[] newArray(int size) {
            return new PermissionGroup[size];
        }
    };

    public List<String> getPermissionList() {
        return permissionList;
    }

    public void setPermissionList(List<String> permissionList) {
        this.permissionList = permissionList;
    }

    public Bundle getExtra() {
        return extra;
    }

    public void setExtra(Bundle extra) {
        this.extra = extra;
    }
}
