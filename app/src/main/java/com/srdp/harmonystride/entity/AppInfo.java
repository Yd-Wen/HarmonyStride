package com.srdp.harmonystride.entity;

public class AppInfo {
    private int versionCode;
    private String oldVersionName;
    private String versionName;
    private String updateLog;
    private String apkUrl;
    private String apkSize;
    private boolean noIgnorable;

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public String getOldVersionName() {
        return oldVersionName;
    }

    public void setOldVersionName(String oldVersionName) {
        this.oldVersionName = oldVersionName;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getUpdateLog() {
        return updateLog;
    }

    public void setUpdateLog(String updateLog) {
        this.updateLog = updateLog;
    }

    public String getApkUrl() {
        return apkUrl;
    }

    public void setApkUrl(String apkUrl) {
        this.apkUrl = apkUrl;
    }

    public String getApkSize() {
        return apkSize;
    }

    public void setApkSize(String apkSize) {
        this.apkSize = apkSize;
    }

    public boolean isNoIgnorable() {
        return noIgnorable;
    }

    public void setNoIgnorable(boolean noIgnorable) {
        this.noIgnorable = noIgnorable;
    }

    @Override
    public String toString() {
        return "AppInfo{" +
                "versionCode=" + versionCode +
                ", oldVersionName='" + oldVersionName + '\'' +
                ", versionName='" + versionName + '\'' +
                ", updateLog='" + updateLog + '\'' +
                ", apkUrl='" + apkUrl + '\'' +
                ", apkSize='" + apkSize + '\'' +
                ", noIgnorable=" + noIgnorable +
                '}';
    }
}
