package com.srdp.harmonystride.entity;

import android.graphics.Bitmap;
import android.os.Message;

import com.srdp.harmonystride.R;
import com.srdp.harmonystride.util.IMUtil;
import com.srdp.harmonystride.util.ImageUtil;
import com.srdp.harmonystride.util.SharedPreferenceUtil;

import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

public class User extends LitePalSupport {
    @Column(unique = true, nullable = false)
    private int uid; //账号（手机号），非空唯一

    @Column(unique = true, nullable = false)
    private String account; //账号（手机号），非空唯一

    @Column(nullable = false)
    private String password; //密码，非空

    @Column(defaultValue = "")
    private String avatar; //用户头像存储路径，默认空

    @Column(nullable = false)
    private String nickname; //昵称，默认为"用户"+账号

    @Column(defaultValue = "0")
    private String certify; //是否认证, 默认 0

    @Column(defaultValue = "保密")
    private String gender; //性别，默认""，可选{"""male""female"}-01-01

    @Column(defaultValue = "未定位")
    private String location; //定位，默认值：""

    @Column(defaultValue = "系统原装签名：与你同行")
    private String introduction; //简介，默认值："系统原装签名：与你同行"

    @Column(defaultValue = "0")
    private String focus; //关注列表，默认为空

    public User(){

    }

    public User(String account, String password) {
        this.account = account;
        this.password = password;
        this.nickname = "User" + account;
        //上传头像
        String imageUrl = "user/" + account + "/" + System.currentTimeMillis() + ".png";
        this.avatar = imageUrl;
        Bitmap image = ImageUtil.getBitmapFromResourceId(R.drawable.default_avatar);
        ImageUtil.upload("", image, imageUrl, null);
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

    @Override
    public String toString() {
        return "User{" +
                "id=" + uid +
                ", account='" + account + '\'' +
                ", password='" + password + '\'' +
                ", avatar='" + avatar + '\'' +
                ", nickname='" + nickname + '\'' +
                ", certify='" + certify + '\'' +
                ", gender='" + gender + '\'' +
                ", location='" + location + '\'' +
                ", introduction='" + introduction + '\'' +
                ", focus='" + focus + '\'' +
                '}';
    }

    public void copyFromUser(User user){
        this.uid = user.getUid();
        this.account = user.getAccount();
        this.password = user.getPassword();
        this.avatar = user.getAvatar();
        this.nickname = user.getNickname();
        this.certify = user.getCertify();
        this.gender = user.getGender();
        this.location = user.getLocation();
        this.introduction = user.getIntroduction();
        this.focus = user.getFocus();
    }

    public String getAvatarUrl(){
        return ImageUtil.getImagePath(avatar);
    }
}
