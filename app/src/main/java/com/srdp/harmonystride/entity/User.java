package com.srdp.harmonystride.entity;

import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

public class User extends LitePalSupport {
//    @Column(unique = true, nullable = false)
//    private int id; //账号（手机号），非空唯一

    @Column(unique = true, nullable = false)
    private String account; //账号（手机号），非空唯一

    @Column(nullable = false)
    private String password; //密码，非空

    @Column(defaultValue = "")
    private String avatar; //用户头像存储路径，默认空

    @Column(nullable = false)
    private String nickname; //昵称，默认为"用户"+账号

    @Column(defaultValue = "no")
    private String certify; //是否认证, 默认 no

    @Column(defaultValue = "")
    private String gender; //性别，默认""，可选{"""male""female"}-01-01

    @Column(defaultValue = "")
    private String location; //定位，默认值：""

    @Column(defaultValue = "系统原装签名：与你同行")
    private String introduction; //简介，默认值："系统原装签名：与你同行"

    @Column(defaultValue = "")
    private String focus; //关注列表，默认为空

    public User(){

    }

    public User(String account, String password) {
        this.account = account;
        this.password = password;
        this.nickname = "User" + account;
        //TODO: 添加默认的管理员Uid
        //focusUid = "";
    }

    public User(String account, String password, String avatar, String nickname, String certify, String sex, String location, String introduction, String focus) {
        this.account = account;
        this.password = password;
        this.avatar = avatar;
        this.nickname = nickname;
        this.certify = certify;
        this.gender = sex;
        this.location = location;
        this.introduction = introduction;
        this.focus = focus;
    }

//    public int getId() {
//        return id;
//    }
//
//    public void setId(int id) {
//        this.id = id;
//    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public String getCertify() {
        return certify;
    }

    public void setCertify(String certify) {
        this.certify = certify;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public String getFocus() {
        return focus;
    }

    public void setFocus(String focus) {
        this.focus = focus;
    }
}
