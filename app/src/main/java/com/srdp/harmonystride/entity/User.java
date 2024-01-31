package com.srdp.harmonystride.entity;

import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

public class User extends LitePalSupport {
    @Column(unique = true, nullable = false)
    private String account; //账号（手机号），非空唯一

    @Column(nullable = false)
    private String password; //密码，非空

    @Column(defaultValue = "")
    private String imageUrl; //用户头像存储路径，默认空

    @Column(nullable = false)
    private String nickname; //昵称，默认为"用户"+账号

    @Column(defaultValue = "0")
    private String certify; //是否认证, 默认0,SQLite无Boolean型

    @Column(defaultValue = "保密")
    private String sex; //性别，默认"保密"，可选{"保密""男""女"}-01-01

    @Column(defaultValue = "未定位")
    private String location; //定位，默认值："未定位"

    @Column(defaultValue = "系统原装签名：与你同行")
    private String introduction; //简介，默认值："系统原装签名：与你同行"

    @Column(defaultValue = "0")
    private String focusUid; //关注列表，默认为空

    public User(){

    }

    public User(String account, String password) {
        this.account = account;
        this.password = password;
        nickname = "user" + account;
        //TODO: 添加默认的管理员Uid
        //focusUid = "";
    }

    public User(String account, String password, String imageUrl, String nickname, String certify, String sex, String location, String introduction, String focusUid) {
        this.account = account;
        this.password = password;
        this.imageUrl = imageUrl;
        this.nickname = nickname;
        this.certify = certify;
        this.sex = sex;
        this.location = location;
        this.introduction = introduction;
        this.focusUid = focusUid;
    }

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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String isCertify() {
        return certify;
    }

    public void setCertify(String certify) {
        this.certify = certify;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
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

    public String getFocusId() {
        return focusUid;
    }

    public void setFocusId(String focusUid) {
        this.focusUid = focusUid;
    }
}
