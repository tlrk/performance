package com.example.momo.performance.layout;

import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.example.performance_android.utils.CommonUtils;
import com.example.performance_android.utils.LogUtils;

/**
 * 一个耗时的 layout
 * Created by tlrk on 8/16/18.
 */
public class JankLayout extends FrameLayout {


    public JankLayout(@NonNull Context context) {
        super(context);
    }

    public JankLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public JankLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        long start = CommonUtils.getRealTime();
        try {
            for (int i =0; i < 10000; i ++) {
                int a = new Integer(i);
            }
        } catch (Exception e) {

        }

        LogUtils.logD("dispatchDraw cost = " + (CommonUtils.getRealTime() - start) + " ms");
        super.dispatchDraw(canvas);
    }
}
