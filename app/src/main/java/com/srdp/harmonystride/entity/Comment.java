package com.srdp.harmonystride.entity;

public class Comment {
    private int owner;
    private int pid;
    private String datetime;
    private String content;

    public Comment() {
    }

    public Comment(int pid, int owner, String datetime, String content) {
        this.pid = pid;
        this.owner = owner;
        this.datetime = datetime;
        this.content = content;
    }

    public int getOwner() {
        return owner;
    }

    public void setOwner(int owner) {
        this.owner = owner;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "owner=" + owner +
                ", pid=" + pid +
                ", datatime='" + datetime + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
