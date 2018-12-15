package com.example.momo.performance.page;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.momo.performance.R;
import com.example.momo.performance.basepage.BaseAppCompatActivity;
import com.example.momo.performance.layout.MomoViewPager;
import com.tlrk.performance.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tlrk on 8/24/18.
 */
public class TestMainActivity extends BaseAppCompatActivity {

    MomoViewPager mViewPager;
    MyViewPagerAdapter mMyViewPagerAdapter;
    private int mCurIndex = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        initView();
    }

    private void initView() {
        mViewPager = findViewById(R.id.view_pager);
        List<ImageView> views = new ArrayList<>();
        for (int i = 0; i< 5; i++) {
            ImageView v = new ImageView(getBaseContext());
            v.setScaleType(ImageView.ScaleType.CENTER_CROP);
            v.setImageResource(R.mipmap.ic_launcher);
            views.add(v);
        }

        mMyViewPagerAdapter = new MyViewPagerAdapter(views);
        mViewPager.setOffscreenPageLimit(2);
        mViewPager.setAdapter(mMyViewPagerAdapter);

    }

    private  Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mCurIndex = (++mCurIndex % 5);
                        LogUtils.logD("==== " + mCurIndex);
                        mViewPager.setCurrentItem(mCurIndex);
                        handler.sendEmptyMessage(0);
                    }
                }, 1000);
            }
            super.handleMessage(msg);
        }
    };

    private class MyViewPagerAdapter extends PagerAdapter {

        List<ImageView> mTextViews;

        MyViewPagerAdapter(List<ImageView> textViews) {
            mTextViews = textViews;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView v = mTextViews.get(position);
            LogUtils.logD("init " + v.toString());
            container.addView(v, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            return v;
        }


        @Override
        public int getCount() {
            return mTextViews.size();
        }


        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(mTextViews.get(position));
        }
    }
}
