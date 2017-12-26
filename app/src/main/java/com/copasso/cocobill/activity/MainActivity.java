package com.copasso.cocobill.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;

import com.bumptech.glide.Glide;
import com.copasso.cocobill.R;
import com.copasso.cocobill.adapter.MainFragmentPagerAdapter;
import com.copasso.cocobill.bean.UserBean;
import com.copasso.cocobill.fragment.MenuAccountFragment;
import com.copasso.cocobill.fragment.MenuChartFragment;
import com.copasso.cocobill.fragment.MenuDetailFragment;
import com.copasso.cocobill.utils.Constants;

import butterknife.BindView;
import butterknife.OnClick;

import java.io.File;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.toolbar)
    android.support.v7.widget.Toolbar toolbar;
    @BindView(R.id.tablayout)
    TabLayout tabLayout;
    @BindView(R.id.main_viewpager)
    ViewPager viewPager;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;
    @BindView(R.id.nav_view)
    NavigationView navigationView;

    private View drawerHeader;
    private ImageView drawerIv;
    private TextView drawerTvAccount, drawerTvMail;

    private static final String TAG = "MainActivity";
    protected static final int USERINFOACTIVITY_CODE = 0;
    protected static final int LOGINACTIVITY_CODE = 1;

    //当前用户
    private UserBean currentUser;

    // Tab
    private FragmentManager mFragmentManager;
    private MainFragmentPagerAdapter mainFragmentPagerAdapter;

    @Override
    protected int getLayout() {
        return R.layout.activity_main;
    }

    @Override
    protected void initEventAndData() {
        //初始化ViewPager
        mFragmentManager = getSupportFragmentManager();
        mainFragmentPagerAdapter = new MainFragmentPagerAdapter(mFragmentManager);
        mainFragmentPagerAdapter.addFragment(new MenuDetailFragment(), "明细");
        mainFragmentPagerAdapter.addFragment(new MenuChartFragment(), "报表");
        mainFragmentPagerAdapter.addFragment(new MenuAccountFragment(), "卡片");

        viewPager.setAdapter(mainFragmentPagerAdapter);

        //初始化TabLayout
        tabLayout.addTab(tabLayout.newTab().setText("明细"));
        tabLayout.addTab(tabLayout.newTab().setText("报表"));
        tabLayout.addTab(tabLayout.newTab().setText("卡片"));
        tabLayout.setupWithViewPager(viewPager);

        //初始化Toolbar
        toolbar.setTitle("CocoBill");
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        drawerHeader = navigationView.inflateHeaderView(R.layout.drawer_header);
        drawerIv = (ImageView) drawerHeader.findViewById(R.id.drawer_iv);
        drawerTvAccount = (TextView) drawerHeader.findViewById(R.id.drawer_tv_name);
        drawerTvMail = (TextView) drawerHeader.findViewById(R.id.drawer_tv_mail);
        //监听点击登陆事件
        drawerHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
                if (Constants.currentUserId == 0) {
                    //用户id为0表示未有用户登陆
                    startActivityForResult(new Intent(MainActivity.this, LoginActivity.class), LOGINACTIVITY_CODE);
                } else {
                    //进入账户中心
                    startActivityForResult(new Intent(MainActivity.this, UserInfoActivity.class), USERINFOACTIVITY_CODE);
                }
            }
        });
        //设置头部账户
        setDrawerHeaderAccount();
        //监听侧滑菜单
        navigationView.setNavigationItemSelectedListener(this);

    }

    /**
     * 监听Drawer
     */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * 监听点击事件 R.id.drawer_tv_name,R.id.drawer_tv_mail
     *
     * @param view
     */
    @OnClick({})
    public void onViewClicked(View view) {
        switch (view.getId()) {

            default:
                break;
        }
    }

    /**
     * 监听Activity返回值
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case USERINFOACTIVITY_CODE:
                    setDrawerHeaderAccount();
                    break;
                case LOGINACTIVITY_CODE:
                    setDrawerHeaderAccount();
                    break;
            }
        }
    }

    /**
     * 设置DrawerHeader的用户信息
     */
    public void setDrawerHeaderAccount() {
        SharedPreferences sp = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        if (sp != null) {
            drawerTvAccount.setText(sp.getString("username", "账户"));
            drawerTvMail.setText(sp.getString("email", "点我登陆"));
            String imgPath=sp.getString("imgPath", null);
            if (imgPath!=null){
                File file = new File(imgPath);
                if (file.exists()){
                    //加载图片
                    Glide.with(this).load(file).into(drawerIv);
                }
            }
            Constants.currentUserId = sp.getInt("id", 0);
        }
    }

    /**
     * 监听侧滑菜单事件
     *
     * @param item
     * @return
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_account) {
            // Handle the camera action
            viewPager.setCurrentItem(0);
        } else if (id == R.id.nav_month) {
            viewPager.setCurrentItem(1);
        } else if (id == R.id.nav_total) {
            viewPager.setCurrentItem(2);
        } else if (id == R.id.nav_setting) {

        } else if (id == R.id.nav_about) {
            startActivity(new Intent(MainActivity.this, AboutActivity.class));
        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_exit) {
            //退出登陆
            new AlertDialog.Builder(mContext).setTitle("是否退出当前账户")
                    .setNegativeButton("取消", null)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //清除登陆信息
                            SharedPreferences sp = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
                            if (sp != null)
                                sp.edit().clear().commit();
                            //刷新账户数据
                            setDrawerHeaderAccount();
                        }
                    })
                    .show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}