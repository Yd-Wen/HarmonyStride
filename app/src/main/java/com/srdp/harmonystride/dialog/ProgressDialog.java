package com.srdp.harmonystride.dialog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.Window;
import android.view.WindowManager;

import com.srdp.harmonystride.MyApplication;
import com.srdp.harmonystride.util.ScreenSizeUtil;

public class ProgressDialog extends BaseDialog{
    private int progress; //当前进度

    public ProgressDialog(Context context) {
        super(context);
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public int getProgress() {
        return progress;
    }
}
