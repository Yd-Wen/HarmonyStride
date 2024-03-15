package com.srdp.harmonystride.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.Spinner;

import androidx.recyclerview.widget.RecyclerView;

import com.srdp.harmonystride.R;

public class SearchActivity extends BaseActivity {

    private SearchView searchView;
    private Button cancelBtn;
    private Spinner spinner;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        //初始化视图
        initViews();
        //初始化事件
        initEvents();
    }

    private void initViews(){
        searchView = findViewById(R.id.search_view);
        cancelBtn = findViewById(R.id.btn_cancel);
        spinner = findViewById(R.id.spinner);
        recyclerView = findViewById(R.id.recycler_view);
    }

    private void initEvents(){
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                //TODO:搜索
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }




}