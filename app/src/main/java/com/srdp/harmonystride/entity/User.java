package com.srdp.harmonystride.entity;

import com.srdp.harmonystride.R;

public class User {
    private int uid; //序号
    private String account; //账号（手机号），非空唯一
    private String password; //密码，非空
    private String imageUrl; //用户头像存储路径，默认空
    private String nickname; //昵称，默认为"用户"+账号
    private boolean certify; //是否认证

    private String sex; //性别，默认"保密"，可选{"保密""男""女"}-01-01
    private String location; //定位，默认值："未定位"
    private String introduction; //简介，默认值："系统原装签名：与你同行"

    private String focusUid; //关注列表，默认为空

    public User(String account, String password) {
        this.account = account;
        this.password = password;
        imageUrl = "";
        nickname = R.string.user + account;
        certify = false;
        sex = String.valueOf(R.string.sex_unknown);
        location = String.valueOf(R.string.location_unknown);
        introduction = String.valueOf(R.string.introduction_default);
        //TODO: 添加默认的管理员Uid
        focusUid = "";
    }

    public User(int uid, String account, String password, String imageUrl, String nickname, boolean certify, String sex, String location, String introduction, String focusUid) {
        this.uid = uid;
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

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
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

    public boolean isCertify() {
        return certify;
    }

    public void setCertify(boolean certify) {
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
