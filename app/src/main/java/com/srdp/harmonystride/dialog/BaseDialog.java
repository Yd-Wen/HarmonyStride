package com.srdp.harmonystride.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.Button;

import com.srdp.harmonystride.MyApplication;
import com.srdp.harmonystride.R;

public class BaseDialog extends Dialog {
    protected Context baseContext;
    private Button cancelBtn;
    private Button confirmBtn;

    public BaseDialog(Context context) {
        super(context, R.style.DialogStyle);
        baseContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void setCancelBtn(Button cancelBtn) {
        this.cancelBtn = cancelBtn;
    }

    public void setConfirmBtn(Button confirmBtn) {
        this.confirmBtn = confirmBtn;
    }

    public Button getCancelBtn() {
        return cancelBtn;
    }

    public Button getConfirmBtn() {
        return confirmBtn;
    }

}
