package com.srdp.harmonystride.entity;

import java.time.LocalDateTime;

public class Chat {
    private String sender; //发送人账号
    private String receiver; //接收人账号
    private String content; //消息
    private LocalDateTime time; //发送时间

    public Chat(String sender, String receiver, String content, LocalDateTime time) {
        this.sender = sender;
        this.receiver = receiver;
        this.content = content;
        this.time = time;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }
}
