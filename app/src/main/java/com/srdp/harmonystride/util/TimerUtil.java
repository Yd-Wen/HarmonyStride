package com.srdp.harmonystride.util;

import android.content.Context;
import android.os.CountDownTimer;
import android.widget.Button;

import com.srdp.harmonystride.R;

public class TimerUtil extends CountDownTimer {
    Context context;
    Button button;
    /**
     * 计时器
     *
     * @param context 通过上下文对象调用getDrawable()方法
     * @param button 更新UI
     */

    public TimerUtil(Context context, Button button, int millisInFuture, int countDownInterval) {
        super(millisInFuture, countDownInterval);
        this.context = context;
        this.button = button;
    }

    @Override
    public void onTick(long l) {
        button.setClickable(false);
        button.setText(l/1000 + "秒后重试");
        button.setBackground(context.getDrawable(R.drawable.selector_shape_button_grey));
    }

    @Override
    public void onFinish() {
        button.setClickable(true);
        button.setText(R.string.verification_code_get);
        button.setBackground(context.getDrawable(R.drawable.selector_shape_button_orange));
    }
}