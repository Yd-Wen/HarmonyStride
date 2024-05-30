package com.srdp.harmonystride.dialog.builder;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.srdp.harmonystride.MyApplication;
import com.srdp.harmonystride.R;
import com.srdp.harmonystride.dialog.BaseDialog;
import com.srdp.harmonystride.dialog.TipDialog;
import com.srdp.harmonystride.dialog.factory.DialogFactory;

import java.util.List;

public class TipDialogBuilder implements DialogBuilder{
    private TipDialog dialog;

    public TipDialogBuilder(Context context){
        dialog = new TipDialog(context);
    }

    @Override
    public DialogBuilder buildView(int viewId) {
        ((FrameLayout)dialog.getView().findViewById(R.id.fl_dialog_container)).addView(View.inflate(MyApplication.getContext(), viewId, null));
        return this;
    }

    @Override
    public DialogBuilder buildIcon(int iconId) {
        dialog.setIcon(iconId);
        return this;
    }

    @Override
    public DialogBuilder buildTitle(String title) {
        dialog.setTitle(title);
        return this;
    }

    @Override
    public DialogBuilder buildContent(String content) {
        dialog.setContent(content);
        return this;
    }

    @Override
    public DialogBuilder buildHint(String hint) {
        return null;
    }

    @Override
    public DialogBuilder buildSigleChoiceItems(List<String> item, List<Integer>resId) {
        return null;
    }

    @Override
    public DialogBuilder buildMultiChoiceItems(List<String> item, List<Integer>resId) {
        return null;
    }

    @Override
    public DialogBuilder buildCancelButton(String cancelText, BaseDialog.MyDialogListener listener) {
        dialog.setCancelText(cancelText);
        dialog.setListener(listener);
        return this;
    }

    @Override
    public DialogBuilder buildConfirmButton(String confirmText, BaseDialog.MyDialogListener listener) {
        dialog.setConfirmText(confirmText);
        dialog.setListener(listener);
        return this;
    }

    @Override
    public DialogBuilder buildLayout(double widthRatio, int gravity) {
        dialog.setSizeRatio(widthRatio);
        dialog.setGravity(gravity);
        return this;
    }

    @SuppressLint("WrongConstant")
    @Override
    public BaseDialog getDialog() {
        ((ImageView)dialog.getView().findViewById(R.id.iv_dialog_icon)).setImageResource(dialog.getIcon());
        ((TextView)dialog.getView().findViewById(R.id.tv_dialog_title)).setText(dialog.getTitle());

        ((TextView)dialog.getView().findViewById(R.id.tv_dialog_content)).setText(dialog.getContent());

        ((Button)dialog.getView().findViewById(R.id.btn_dialog_cancel)).setText(dialog.getCancelText());
        ((Button)dialog.getView().findViewById(R.id.btn_dialog_confirm)).setText(dialog.getConfirmText());
        if(dialog.getTitle().equals(DialogFactory.DIALOG_TITLE_UPDATE_NEED_MUST)) dialog.getView().findViewById(R.id.btn_dialog_cancel).setVisibility(View.GONE);
        if(dialog.getTitle().equals(DialogFactory.DIALOG_TITLE_UPDATE_DOING)) dialog.getView().findViewById(R.id.btn_dialog_confirm).setVisibility(View.GONE);

        ((Button)dialog.getView().findViewById(R.id.btn_dialog_cancel)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.getListener().onClick(false, null);
                dialog.dismiss();
            }
        });

        ((Button)dialog.getView().findViewById(R.id.btn_dialog_confirm)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.getListener().onClick(true, null);
                dialog.dismiss();
            }
        });
        return dialog;
    }
}
