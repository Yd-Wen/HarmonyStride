package com.srdp.harmonystride.dialog.builder;

import com.srdp.harmonystride.dialog.BaseDialog;

import java.util.List;

public interface DialogBuilder {
    DialogBuilder buildView(int viewId); //视图

    DialogBuilder buildIcon(int iconId); //图标
    DialogBuilder buildTitle(String title); //标题

    DialogBuilder buildContent(String content); // 内容
    DialogBuilder buildHint(String hint); // 提示
    DialogBuilder buildSigleChoiceItems(List<String> item, List<Integer>resId); //单选
    DialogBuilder buildMultiChoiceItems(List<String> item, List<Integer>resId); //多选

    DialogBuilder buildCancelButton(String cancelText, BaseDialog.MyDialogListener listener); //取消按钮
    DialogBuilder buildConfirmButton(String confirmText, BaseDialog.MyDialogListener listener); //确认按钮

    DialogBuilder buildLayout(double widthRatio, int gravity); //布局

    BaseDialog getDialog();
}
