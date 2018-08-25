package com.example.momo.performance.basepage;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;

import com.example.performance_android.AutoSpeed;

/**
 * Created by tlrk on 8/16/18.
 */
public class BaseAppCompatActivity extends AppCompatActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onStart() {
//        LogUtils.logD("onStart");
        super.onStart();
    }

    @Override
    protected void onResume() {
//        LogUtils.logD("onResume");
        super.onResume();
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(AutoSpeed.getInstance().createPageView(this, view));
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(AutoSpeed.getInstance().createPageView(this, layoutResID));
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        super.setContentView(AutoSpeed.getInstance().createPageView(this, view, params));
    }

}
