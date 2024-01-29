package com.srdp.harmonystride.entity;

public class Post {
    private int pid; //序号
    private String owner; //发布人（账号/手机号），非空唯一
    private String datetime; //发布时间，非空
    private String title; //标题，非空
    private String content; //内容，非空
    private String lid[]; //标签列表，默认“其他”
    private String image; //图片路径
    private String receiver; //接受人（账号）
}
