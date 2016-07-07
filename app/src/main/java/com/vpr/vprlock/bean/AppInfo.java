package com.vpr.vprlock.bean;

import java.io.Serializable;

import android.graphics.drawable.Drawable;

import net.tsz.afinal.annotation.sqlite.Id;

public class AppInfo implements Serializable {

	@Id(column = "packageName")
	private String packageName;
	private String appName;
	private String versionName;
	private int versionCode = 0;
	private Drawable appIcon = null;
	private boolean selected = false;

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getVersionName() {
		return versionName;
	}

	public void setVersionName(String versionName) {
		this.versionName = versionName;
	}

	public int getVersionCode() {
		return versionCode;
	}

	public void setVersionCode(int versionCode) {
		this.versionCode = versionCode;
	}

	public Drawable getAppIcon() {
		return appIcon;
	}

	public void setAppIcon(Drawable appIcon) {
		this.appIcon = appIcon;
	}

}
