package com.srdp.harmonystride.entity;

public class Certification {
    //private int cid; //序号
    private int uid; //用户ID，非空唯一
    private String number; //证件号码，非空唯一
    private String type; //认证内容，可选{"志愿者""残疾人""用人单位"“管理员”}
    private String imageUrl; //照片路径，非空唯一
    private String status; //审核状态 {"待审核""不通过""通过"}，默认待审核
    private String info; //审核信息 非空，默认空字符串，默认值待审核

    public Certification() {
    }

    public Certification(int uid, String number, String type, String imageUrl, String status, String info) {
        this.uid = uid;
        this.number = number;
        this.type = type;
        this.imageUrl = imageUrl;
        this.status = status;
        this.info = info;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }
}
