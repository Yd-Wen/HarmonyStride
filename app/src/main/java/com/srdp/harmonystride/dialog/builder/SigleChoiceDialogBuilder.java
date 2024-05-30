package com.srdp.harmonystride.dialog.builder;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.srdp.harmonystride.MyApplication;
import com.srdp.harmonystride.R;
import com.srdp.harmonystride.dialog.BaseDialog;
import com.srdp.harmonystride.dialog.SigleChoiceDialog;
import com.srdp.harmonystride.util.ImageUtil;

import java.util.List;

public class SigleChoiceDialogBuilder implements DialogBuilder{
    private SigleChoiceDialog dialog;

    public SigleChoiceDialogBuilder(Context context)
    {
        dialog = new SigleChoiceDialog(context);
    }

    @Override
    public DialogBuilder buildView(int viewId) {
        ((FrameLayout)dialog.getView().findViewById(R.id.fl_dialog_container)).addView(View.inflate(MyApplication.getContext(), viewId, null));
        return this;
    }

    @Override
    public DialogBuilder buildIcon(int iconId) {
        dialog.setIcon(iconId);
        return this;
    }

    @Override
    public DialogBuilder buildTitle(String title) {
        dialog.setTitle(title);
        return this;
    }

    @Override
    public DialogBuilder buildContent(String content) {
        dialog.setContent(content);
        return this;
    }

    @Override
    public DialogBuilder buildHint(String hint) {
        return null;
    }

    @Override
    public DialogBuilder buildSigleChoiceItems(List<String> item, List<Integer>resId) {
        dialog.setItem(item);
        dialog.setResId(resId);
        return this;
    }

    @Override
    public DialogBuilder buildMultiChoiceItems(List<String> item, List<Integer>resId) {
        return null;
    }

    @Override
    public DialogBuilder buildCancelButton(String cancelText, BaseDialog.MyDialogListener listener) {
        dialog.setCancelText(null);
        dialog.setListener(listener);
        return this;
    }

    @Override
    public DialogBuilder buildConfirmButton(String confirmText, BaseDialog.MyDialogListener listener) {
        dialog.setConfirmText(null);
        dialog.setListener(listener);
        return this;
    }

    @Override
    public DialogBuilder buildLayout(double widthRatio, int gravity) {
        dialog.setSizeRatio(widthRatio);
        dialog.setGravity(gravity);
        return this;
    }

    @Override
    public BaseDialog getDialog() {
        ((ImageView)dialog.getView().findViewById(R.id.iv_dialog_icon)).setImageResource(dialog.getIcon());
        ((TextView)dialog.getView().findViewById(R.id.tv_dialog_title)).setText(dialog.getTitle());

        RadioGroup radioGroup = dialog.getView().findViewById(R.id.rg_dialog_content);

        ImageView cancelIv = dialog.getView().findViewById(R.id.iv_dialog_cancel);
        ImageView confirmIv = dialog.getView().findViewById(R.id.iv_dialog_confirm);
        confirmIv.setVisibility(View.GONE);

        for(int i = 0; i < dialog.getItem().size(); i++){
            RadioButton radioButton = new RadioButton(MyApplication.getContext());
            RadioGroup.LayoutParams layoutParams = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.MATCH_PARENT, RadioGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.topMargin = 2;
            layoutParams.bottomMargin = 2;
            radioButton.setLayoutParams(layoutParams);
            radioButton.setPadding(30, 10, 30, 10);
            radioButton.setBackground(ImageUtil.getDrawableFromResourceId(R.drawable.selector_shape_radio_button));
            radioButton.setCompoundDrawablesRelativeWithIntrinsicBounds(ImageUtil.getDrawableFromResourceId(dialog.getResId().get(i)), null, null, null);
            radioButton.setCompoundDrawablePadding(50);
            radioButton.setButtonDrawable(null);
            radioButton.setTextAppearance(R.style.TextAppearance);
            radioButton.setText(dialog.getItem().get(i));
            radioGroup.addView(radioButton);
            if(dialog.getContent().equals(radioButton.getText().toString())){
                radioButton.setChecked(true);
            }

        }

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int id) {
                RadioButton radioButton = dialog.getView().findViewById(id);
                if(radioButton != null){
                    if(!dialog.getContent().equals(radioButton.getText().toString())){
                        cancelIv.setVisibility(View.GONE);
                        confirmIv.setVisibility(View.VISIBLE);
                    }else {
                        cancelIv.setVisibility(View.VISIBLE);
                        confirmIv.setVisibility(View.GONE);
                    }
                }
            }
        });


        cancelIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.getListener().onClick(false, null);
                dialog.dismiss();
            }
        });

        confirmIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RadioButton radioButton = radioGroup.findViewById(radioGroup.getCheckedRadioButtonId());
                dialog.getListener().onClick(true, radioButton.getText().toString());
                dialog.dismiss();
            }
        });

        return dialog;
    }
}
