package com.cdw.zhihutopnews;


import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.util.SimpleArrayMap;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.cdw.zhihutopnews.activity.BaseActivity;
import com.cdw.zhihutopnews.fragment.ZhihuFragment;
import butterknife.BindView;
import butterknife.ButterKnife;
public class MainActivity extends BaseActivity {


    SimpleArrayMap<Integer, String> titleArryMap = new SimpleArrayMap<>();

    @BindView(R.id.fragment_container)
    FrameLayout fragmentContainer;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.nav_view)
    NavigationView navView;
    @BindView(R.id.drawer)
    DrawerLayout drawer;


    private MenuItem currentMenuItem;
    private Fragment currentFragment;
    private long exitTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(navigationOnClickListener);
        getSupportActionBar().setDisplayShowTitleEnabled(false);//去掉默认显示的Title
       addFragmentAndTitle();

        drawer.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE//保持整个View稳定, 常和控制System UI悬浮, 隐藏的Flags共用, 使
                        // View不会因为System UI的变化而重新layout
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN//视图延伸至状态栏区域，状态栏上浮于视图之上
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);//视图延伸至导航栏区域，导航栏上浮于视图之上


        if (savedInstanceState == null) {

            if (currentMenuItem == null) {
                currentMenuItem = navView.getMenu().findItem(R.id.zhihuitem);//默认选择知乎界面
            }
            if (currentMenuItem != null) {
                currentMenuItem.setChecked(true);
                Fragment fragment = getFragmentById(currentMenuItem.getItemId());
                if (fragment != null) {
                    switchFragment(fragment);
                }
            }
        } else {
            if (currentMenuItem != null) {
                Fragment fragment = getFragmentById(currentMenuItem.getItemId());
                if (fragment != null) {
                    switchFragment(fragment);
                }
            } else {
                switchFragment(new ZhihuFragment());
                currentMenuItem = navView.getMenu().findItem(R.id.zhihuitem);
            }
        }
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (currentMenuItem != item && currentMenuItem != null) {
                    currentMenuItem.setChecked(false);
                    currentMenuItem = item;
                    currentMenuItem.setChecked(true);
                    switchFragment(getFragmentById(currentMenuItem.getItemId()));
                }
                drawer.closeDrawer(GravityCompat.START, true);
                return true;
            }
        });


        int[][] state = new int[][]{
                new int[]{-android.R.attr.state_checked}, // unchecked
                new int[]{android.R.attr.state_checked}  // pressed
        };
        int[] color = new int[]{
                Color.BLACK, Color.BLACK};
        int[] iconcolor = new int[]{
                Color.GRAY, Color.BLACK};
        navView.setItemTextColor(new ColorStateList(state, color));
        navView.setItemIconTintList(new ColorStateList(state, iconcolor));
    }


    /**
     * 切换不同的Fragment
     *
     * @param fragment
     */
    private void switchFragment(Fragment fragment) {
        if (currentFragment == null || !currentFragment.getClass().getName().equals(fragment.getClass().getName()))
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment)
                    .commit();
        currentFragment = fragment;
    }

    /**
     * 根据ID加载不同的Fragment
     *
     * @param itemId
     * @return
     */
    private Fragment getFragmentById(int itemId) {
        Fragment fragment = null;
        switch (itemId) {
            case R.id.zhihuitem:
                fragment = new ZhihuFragment();
                break;
        }
        return fragment;

    }

    private void addFragmentAndTitle() {
        titleArryMap.put(R.id.zhihuitem, getResources().getString(R.string.zhihu));

    }

    private View.OnClickListener navigationOnClickListener=new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            drawer.openDrawer(GravityCompat.START);
        }
    };



    //    when recycle view scroll bottom,need loading more date and show the more view.
    public interface LoadingMore {
        void loadingStart();
        void loadingFinish();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if((System.currentTimeMillis()- exitTime)>2000){
                Toast.makeText(MainActivity.this, R.string.app_exit_tip, Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            }else{
                super.onBackPressed();
            }
        }
    }
}
