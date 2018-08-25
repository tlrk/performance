package com.example.momo.performance.layout;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class MomoViewPager extends ViewPager {
    /**
     * 是否可以水平滑动页面
     */
    private boolean scrollHorizontalEnabled = true;

    public MomoViewPager(Context context) {
        super(context);
    }

    public MomoViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    /**
     * 设置是否允许滑动
     *
     * @param enableTouchScroll
     */
    public void setScrollHorizontalEnabled(boolean enableTouchScroll) {
        this.scrollHorizontalEnabled = enableTouchScroll;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        //try catch 是为了避免部分手机Touch事件导致的崩溃
        try {
            return scrollHorizontalEnabled && super.onTouchEvent(ev);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return true;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        try {
            return scrollHorizontalEnabled && super.onInterceptTouchEvent(ev);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }
}