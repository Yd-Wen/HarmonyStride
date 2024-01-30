package com.srdp.harmonystride.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class BaseDialog extends Dialog {

    private TextView contentTv;
    private Button cancelBtn;
    private Button confirmBtn;

    public BaseDialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void setContentTv(TextView contentTv) {
        this.contentTv = contentTv;
    }

    public void setCancelBtn(Button cancelBtn) {
        this.cancelBtn = cancelBtn;
    }

    public void setConfirmBtn(Button confirmBtn) {
        this.confirmBtn = confirmBtn;
    }

    public TextView getContentTv() {
        return contentTv;
    }

    public Button getCancelBtn() {
        return cancelBtn;
    }

    public Button getConfirmBtn() {
        return confirmBtn;
    }

}
