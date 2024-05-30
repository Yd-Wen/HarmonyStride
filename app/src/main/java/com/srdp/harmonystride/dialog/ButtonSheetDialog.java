package com.srdp.harmonystride.dialog;

import android.content.Context;
import java.util.ArrayList;
import java.util.List;

public class ButtonSheetDialog extends BaseDialog {
    private List<String> item = new ArrayList<>();

    public ButtonSheetDialog(Context context) {
        super(context);
    }

    public List<String> getItem() {
        return item;
    }

    public void setItem(List<String> item) {
        this.item = item;
    }
}
