package com.srdp.harmonystride.entity;

import java.time.LocalDateTime;

public class Conversation {
    private String avatar; //发送人头像
    private String nickname; //发送人昵称
    private String message; //最近发送的一条消息
    private LocalDateTime time; //发送时间

    public Conversation(String avatar, String nickname, String message, LocalDateTime time) {
        this.avatar = avatar;
        this.nickname = nickname;
        this.message = message;
        this.time = time;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }
}
