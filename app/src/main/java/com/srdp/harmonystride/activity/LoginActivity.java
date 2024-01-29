package com.srdp.harmonystride.activity;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.srdp.harmonystride.R;
import com.srdp.harmonystride.util.HTTPUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class LoginActivity extends BaseActivity {

    private Button loginButton;
    private EditText account;
    private EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //初始化视图
        initViews();
        //初始化事件
        initEvents();

    }


    private void initViews() {
        loginButton = findViewById(R.id.btn_login);
        account = findViewById(R.id.et_account);
        password = findViewById(R.id.et_password);
    }

    private void initEvents(){
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                navigateTo(MainActivity.class);
//                finish();
                Toast.makeText(LoginActivity.this,"请求中", Toast.LENGTH_SHORT).show();
                Map map=new HashMap();
                map.put("account",account.getText().toString());
                map.put("password",password.getText().toString());
                String url= "/user/login";
                HTTPUtil.POST(url, "", map, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.e(TAG, e.toString());
                    }
                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        nextAction(response.body().string());
                    }
                });

            }
        });
    }

    private void nextAction(String statue){
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject jsonObject = new JSONObject(statue);
                    String re=jsonObject.getString("status");
                    if(re.equals("1")){
                        re = jsonObject.getString("user");
                        Toast.makeText(LoginActivity.this,re,Toast.LENGTH_LONG).show();
                        navigateTo(MainActivity.class);
                        finish();
                    }else{
                        re = jsonObject.getString("info");
                        Toast.makeText(LoginActivity.this,re,Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    Toast.makeText(LoginActivity.this,e.toString(),Toast.LENGTH_LONG).show();
                }
            }
        });
    }

}

