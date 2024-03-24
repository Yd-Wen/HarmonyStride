package com.srdp.harmonystride.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
//import com.hyphenate.easeui.constants.EaseConstant;
//import com.hyphenate.easeui.modules.chat.EaseChatFragment;
import com.srdp.harmonystride.MyApplication;
import com.srdp.harmonystride.R;
import com.srdp.harmonystride.entity.Result;
import com.srdp.harmonystride.entity.User;
import com.srdp.harmonystride.util.HTTPUtil;
import com.srdp.harmonystride.util.ImageUtil;

import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ChatActivity extends BaseActivity {

}