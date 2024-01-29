package com.srdp.harmonystride.entity;

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

    private int focusId[]; //关注列表，默认为空

}
