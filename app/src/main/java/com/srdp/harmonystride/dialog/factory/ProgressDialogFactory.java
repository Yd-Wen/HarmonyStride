package com.srdp.harmonystride.dialog.factory;

import android.content.Context;
import android.view.Gravity;

import com.srdp.harmonystride.R;
import com.srdp.harmonystride.dialog.BaseDialog;
import com.srdp.harmonystride.dialog.builder.ProgressDialogBuilder;
import com.srdp.harmonystride.dialog.builder.TipDialogBuilder;

import java.util.List;

public class ProgressDialogFactory implements DialogFactory{
    @Override
    public BaseDialog createDialog(Context context, String title, List<String> data, List<Integer> resId, BaseDialog.MyDialogListener listener) {
        BaseDialog dialog = new ProgressDialogBuilder(context)
                .buildView(R.layout.dialog_progress)
                .buildIcon(R.drawable.dialog_icon)
                .buildTitle(title)
                .buildCancelButton(DIALOG_CANCEL_TEXT_DEFAULT, listener)
                .buildConfirmButton(DIALOG_CONFIRM_TEXT_DEFAULT, listener)
                .buildLayout(0.8, Gravity.CENTER)
                .getDialog();

        return dialog;
    }
}
