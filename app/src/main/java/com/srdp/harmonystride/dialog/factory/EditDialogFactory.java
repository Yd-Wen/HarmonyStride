package com.srdp.harmonystride.dialog.factory;

import android.content.Context;
import android.view.Gravity;

import com.srdp.harmonystride.R;
import com.srdp.harmonystride.dialog.BaseDialog;
import com.srdp.harmonystride.dialog.builder.EditDialogBuilder;

import java.util.List;

public class EditDialogFactory implements DialogFactory {
    @Override
    public BaseDialog createDialog(Context context, String title, List<String> data, List<Integer> resId, BaseDialog.MyDialogListener listener) {
        BaseDialog dialog = new EditDialogBuilder(context)
                .buildView(R.layout.dialog_edit)
                .buildIcon(R.drawable.dialog_icon)
                .buildTitle(title)
                .buildContent(data.get(0))
                .buildHint(data.get(1))
                .buildCancelButton(DIALOG_CANCEL_TEXT_DEFAULT, listener)
                .buildConfirmButton(DIALOG_CONFIRM_TEXT_DEFAULT, listener)
                .buildLayout(0.8, Gravity.CENTER)
                .getDialog();

        return dialog;
    }
}
