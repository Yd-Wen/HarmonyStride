package com.srdp.harmonystride.util;

import android.view.MenuItem;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.srdp.harmonystride.R;

public class ScrollUtil {
    private BottomNavigationView bottomNavigationView;
    private RecyclerView recyclerView;

    public ScrollUtil(BottomNavigationView bottomNavigationView, RecyclerView recyclerView){
        this.bottomNavigationView = bottomNavigationView;
        this.recyclerView = recyclerView;
    }

    public void setItem(int icon, int title) {
        MenuItem menuItem = bottomNavigationView.getMenu().findItem(R.id.home);
        menuItem.setIcon(icon);
        menuItem.setTitle(title);
    }

    public int getScrollOffset() {
        return recyclerView.computeVerticalScrollOffset();
    }

    public void scrollToTop() {
        recyclerView.smoothScrollToPosition(0);
    }

}
