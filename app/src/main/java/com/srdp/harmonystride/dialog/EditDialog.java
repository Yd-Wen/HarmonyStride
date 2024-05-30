package com.srdp.harmonystride.dialog;

import android.content.Context;

public class EditDialog extends BaseDialog {
    private String hint;
    private String content;

    public EditDialog(Context context) {
        super(context);
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
