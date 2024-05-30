package com.srdp.harmonystride.activity;

import android.os.Bundle;
import android.view.View;

import com.srdp.harmonystride.R;
import com.srdp.harmonystride.dialog.BaseDialog;
import com.srdp.harmonystride.dialog.factory.DialogFactory;
import com.srdp.harmonystride.dialog.factory.TipDialogFactory;

import java.util.ArrayList;
import java.util.List;

public class TestActivity extends BaseActivity {

    DialogFactory factory;
    BaseDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

       findViewById(R.id.btn1).setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {

               factory = new TipDialogFactory();
               List<String> data = new ArrayList<>();
               data.add("content");
               dialog = factory.createDialog(TestActivity.this, DialogFactory.DIALOG_TITLE_POST_ADD, data, null, new BaseDialog.MyDialogListener() {
                   @Override
                   public void onClick(Boolean isConfirm, String data) {
                       if(isConfirm) {
                           showToast("confirm");
                       } else {
                           showToast("cancel");
                       }
                   }
               });
               dialog.show();
           }
       });

        findViewById(R.id.btn2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        findViewById(R.id.btn3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }
}