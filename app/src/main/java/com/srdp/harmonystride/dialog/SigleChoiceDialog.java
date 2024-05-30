package com.srdp.harmonystride.dialog;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

public class SigleChoiceDialog extends BaseDialog {
    private String content;
    private List<Integer> resId = new ArrayList<>();
    private List<String> item = new ArrayList<>();

    public SigleChoiceDialog(Context context) {
        super(context);
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<Integer> getResId() {
        return resId;
    }

    public void setResId(List<Integer> resId) {
        this.resId = resId;
    }

    public List<String> getItem() {
        return item;
    }

    public void setItem(List<String> item) {
        this.item = item;
    }

}
