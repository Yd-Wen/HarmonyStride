package com.srdp.harmonystride.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class BaseDialog extends Dialog {

    private Button cancelBtn;
    private Button confirmBtn;

    public BaseDialog(Context context) {
        super(context);
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
