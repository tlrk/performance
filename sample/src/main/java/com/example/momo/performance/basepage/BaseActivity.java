package com.example.momo.performance.basepage;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewGroup;

import com.example.performance_android.AutoSpeed;

/**
 * Created by momo on 8/6/18.
 */

public class BaseActivity extends FragmentActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AutoSpeed.getInstance().onPageCreate(this);
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
