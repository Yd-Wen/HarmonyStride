package com.srdp.harmonystride.dialog.factory;

import android.content.Context;
import android.view.Gravity;
import com.srdp.harmonystride.R;
import com.srdp.harmonystride.dialog.BaseDialog;
import com.srdp.harmonystride.dialog.builder.ButtonSheetDialogBuilder;
import java.util.List;

public class ButtonSheetDialogFactory implements DialogFactory{
    @Override
    public BaseDialog createDialog(Context context, String title, List<String> data, List<Integer> resId, BaseDialog.MyDialogListener listener) {
        BaseDialog dialog = new ButtonSheetDialogBuilder(context)
                .buildView(R.layout.dialog_button_sheet)
                .buildIcon(0)
                .buildTitle(null)
                .buildSigleChoiceItems(data, resId)
                .buildCancelButton(DIALOG_CANCEL_TEXT_DEFAULT, listener)
                .buildLayout(0.9, Gravity.BOTTOM)
                .getDialog();
        return dialog;
    }
}
