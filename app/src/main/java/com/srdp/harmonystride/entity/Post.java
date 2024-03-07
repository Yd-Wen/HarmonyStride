package com.srdp.harmonystride.entity;

public class Post {
    private int pid; //序号
    private int owner; //发布人（uid），非空唯一
    private String datetime; //发布时间，非空
    private String title; //标题，非空
    private String content; //内容，非空
    private String labels; //标签列表，默认“其他”
    private String images; //图片路径
    private int receiver; //接受人（uid）
}
