package com.srdp.harmonystride.dialog.factory;

import android.content.Context;
import android.view.Gravity;

import com.srdp.harmonystride.R;
import com.srdp.harmonystride.dialog.BaseDialog;
import com.srdp.harmonystride.dialog.builder.SigleChoiceDialogBuilder;

import java.util.List;

public class SigleChoiceDialogFactory implements DialogFactory{
    @Override
    public BaseDialog createDialog(Context context, String title, List<String> data, List<Integer> resId, BaseDialog.MyDialogListener listener) {
        BaseDialog dialog = new SigleChoiceDialogBuilder(context)
                .buildView(R.layout.dialog_sigle_choice)
                .buildIcon(R.drawable.dialog_icon)
                .buildTitle(title)
                .buildContent(data.get(0))
                .buildSigleChoiceItems(data.subList(1, data.size()), resId)
                .buildCancelButton(null, listener)
                .buildConfirmButton(null, listener)
                .buildLayout(1.0, Gravity.BOTTOM)
                .getDialog();

        return dialog;
    }
}
