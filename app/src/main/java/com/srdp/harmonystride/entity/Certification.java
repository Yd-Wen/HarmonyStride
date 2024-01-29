package com.srdp.harmonystride.entity;

public class Certification {
    private int cid; //序号
    private String number; //证件号码，非空唯一
    private String ccontent; //认证内容，可选{"志愿者""残疾人""用人单位"}
    private String imageUrl; //照片路径，非空唯一
    private int uid; //用户ID，非空唯一
    private boolean agree; //审核是否通过
}
