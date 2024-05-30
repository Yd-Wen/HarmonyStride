package com.srdp.harmonystride.dialog.factory;

import android.content.Context;
import com.srdp.harmonystride.dialog.BaseDialog;
import java.util.List;

public interface DialogFactory {
    String DIALOG_TITLE_PRIVACY_AUTHORIZE = "隐私政策授权提示";

    String DIALOG_TITLE_UPDATE_NEED_MUST = "版本更新";
    String DIALOG_TITLE_UPDATE_NEED_OPTIONAL = "版本更新提示";
    String DIALOG_TITLE_UPDATE_DOING = "新版本下载中";

    String DIALOG_TITLE_ACCOUNT_CERTIFY = "账号认证提示";
    String DIALOG_TITLE_ACCOUNT_LOGOUT = "账号登出提示";
    String DIALOG_TITLE_ACCOUNT_DELETE = "账号注销提示";

    String DIALOG_TITLE_POST_ADD = "帖子发布提示";
    String DIALOG_TITLE_POST_DELETE = "帖子删除提示";

    String DIALOG_TITLE_APPLY_AGREE = "申请接受提示";

    String DIALOG_TITLE_ACCOUNT_NICKNAME = "昵称";
    String DIALOG_HINT_ACCOUNT_NICKNAME = "请输入昵称";

    String DIALOG_TITLE_ACCOUNT_GENDER = "性别";

    String DIALOG_TITLE_ACCOUNT_INTRODUCTION = "签名";
    String DIALOG_HINT_ACCOUNT_INTRODUCTION = "请输入签名";

    String DIALOG_TITLE_CERTIFY_TYPE = "认证类型";
    String DIALOG_TITLE_CERTIFY_DISABLED_NUMBER = "残疾证号";
    String DIALOG_HINT_CERTIFY_DISABLED_NUMBER = "请输入残疾证号";
    String DIALOG_TITLE_CERTIFY_VOLUNTEER_NUMBER = "身份证号";
    String DIALOG_HINT_CERTIFY_VOLUNTEER_NUMBER = "请输入身份证号";
    String DIALOG_TITLE_CERTIFY_EMPLOYER_NUMBER = "信用代码";
    String DIALOG_HINT_CERTIFY_EMPLOYER_NUMBER = "请输入信用代码";
    String DIALOG_TITLE_CERTIFY_ADMIN_NUMBER = "身份证号";
    String DIALOG_HINT_CERTIFY_ADMIN_NUMBER = "请输入身份证号";

    String DIALOG_TITLE_APPLY_REASON = "申请理由";
    String DIALOG_HINT_APPLY_REASON = "请输入申请理由";

    String DIALOG_CANCEL_TEXT_DEFAULT = "取消";
    String DIALOG_CONFIRM_TEXT_DEFAULT = "确定";

    BaseDialog createDialog(Context context, String title, List<String> data, List<Integer> resId, BaseDialog.MyDialogListener listener);
}
