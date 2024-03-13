package com.srdp.harmonystride.entity;

import java.io.Serializable;

public class Post implements Serializable {
    private int pid; //序号
    private int owner; //发布人（uid），非空唯一
    private String datetime; //发布时间，非空
    private String title; //标题，非空
    private String content; //内容，非空
    private String label; //标签列表，默认“其他”
    private String images; //图片路径
    private Integer receiver; //接受人（uid）
    private String allow; //是否允许申请

    public Post() {
        allow = "1";
    }

    public Post(int pid, int owner, String datetime, String title, String content, String label, String images, Integer receiver, String allow) {
        this.pid = pid;
        this.owner = owner;
        this.datetime = datetime;
        this.title = title;
        this.content = content;
        this.label = label;
        this.images = images;
        this.receiver = receiver;
        this.allow = allow;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public int getOwner() {
        return owner;
    }

    public void setOwner(int owner) {
        this.owner = owner;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }

    public Integer getReceiver() {
        return receiver;
    }

    public void setReceiver(Integer receiver) {
        this.receiver = receiver;
    }

    public String getAllow() {
        return allow;
    }

    public void setAllow(String allow) {
        this.allow = allow;
    }

    @Override
    public String toString() {
        return "Post{" +
                "pid=" + pid +
                ", owner=" + owner +
                ", datetime='" + datetime + '\'' +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", label='" + label + '\'' +
                ", images='" + images + '\'' +
                ", receiver=" + receiver +
                ", allow='" + allow + '\'' +
                '}';
    }
}
