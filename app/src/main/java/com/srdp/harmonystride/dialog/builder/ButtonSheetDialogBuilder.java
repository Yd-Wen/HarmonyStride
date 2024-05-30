package com.srdp.harmonystride.dialog.builder;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.srdp.harmonystride.MyApplication;
import com.srdp.harmonystride.R;
import com.srdp.harmonystride.dialog.ButtonSheetDialog;
import com.srdp.harmonystride.dialog.BaseDialog;
import com.srdp.harmonystride.util.PermissionUtil;
import java.util.List;

public class ButtonSheetDialogBuilder implements DialogBuilder{
    private ButtonSheetDialog dialog;

    public ButtonSheetDialogBuilder(Context context)
    {
        dialog = new ButtonSheetDialog(context);
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
        return null;
    }

    @Override
    public DialogBuilder buildHint(String hint) {
        return null;
    }

    @Override
    public DialogBuilder buildSigleChoiceItems(List<String> item, List<Integer> resId) {
        dialog.setItem(item);
        return this;
    }

    @Override
    public DialogBuilder buildMultiChoiceItems(List<String> item, List<Integer> resId) {
        return null;
    }

    @Override
    public DialogBuilder buildCancelButton(String cancelText, BaseDialog.MyDialogListener listener) {
        dialog.setCancelText(cancelText);
        dialog.setListener(listener);
        return this;
    }

    @Override
    public DialogBuilder buildConfirmButton(String confirmText, BaseDialog.MyDialogListener listener) {
        return null;
    }

    @Override
    public DialogBuilder buildLayout(double widthRatio, int gravity) {
        dialog.setSizeRatio(widthRatio);
        dialog.setGravity(gravity);
        return this;
    }

    @Override
    public BaseDialog getDialog() {
        if(dialog.getIcon() == 0) {
            dialog.getView().findViewById(R.id.iv_dialog_icon).setVisibility(View.GONE);
        }else ((ImageView)dialog.getView().findViewById(R.id.iv_dialog_icon)).setImageResource(dialog.getIcon());
        if(dialog.getTitle() == null) {
            dialog.getView().findViewById(R.id.tv_dialog_title).setVisibility(View.GONE);
        }else ((TextView)dialog.getView().findViewById(R.id.tv_dialog_title)).setText(dialog.getTitle());

        LinearLayout llDialogBody = (LinearLayout)dialog.getView().findViewById(R.id.ll_dialog_body);

        for(int i = 0; i < dialog.getItem().size() + 1; i++){
            Button button = new Button(MyApplication.getContext());
            button.setTextAppearance(R.style.TitleAppearance);
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
            if(i == dialog.getItem().size()){
                button.setText(dialog.getCancelText());
                layoutParams.topMargin = 20;
                layoutParams.bottomMargin = 10;
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.getListener().onClick(false, null);
                        dialog.dismiss();
                    }
                });
            }else {
                button.setText(dialog.getItem().get(i));
                layoutParams.topMargin = 5;
                layoutParams.bottomMargin = 5;
                if(dialog.getItem().get(i).equals(PermissionUtil.TYPE_PHOTO_ALBUM)){
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.getListener().onClick(true, PermissionUtil.TYPE_PHOTO_ALBUM);
                            dialog.dismiss();
                        }
                    });
                }else if(dialog.getItem().get(i).equals(PermissionUtil.TYPE_OPEN_CAMERA)){
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.getListener().onClick(true, PermissionUtil.TYPE_OPEN_CAMERA);
                            dialog.dismiss();
                        }
                    });
                }

            }
            button.setLayoutParams(layoutParams);
            button.setBackgroundResource(R.drawable.selector_shape_button_white);
            llDialogBody.addView(button);
        }
        dialog.getView().setBackgroundResource(android.R.color.transparent);
        return dialog;
    }
}
