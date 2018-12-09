package com.immomo.performance.pagespeed;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Build;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * Created by tlrk on 8/16/18.
 */
public class AutoSpeedFrameLayout extends FrameLayout {

    private int pageObjectKey;//关联的页面key

    public AutoSpeedFrameLayout(@NonNull Context context) {
        super(context);
    }

    public AutoSpeedFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public AutoSpeedFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public AutoSpeedFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public static View wrap(int pageObjectKey, @NonNull View child) {
        //将页面根View作为子View，其他参数保持不变
        ViewGroup vg = new AutoSpeedFrameLayout(child.getContext(), pageObjectKey);
        if (child.getLayoutParams() != null) {
            vg.setLayoutParams(child.getLayoutParams());
        }
        vg.addView(child, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        return vg;
    }

    public static View wrap(int pageObjectKey, @NonNull View child, ViewGroup.LayoutParams params) {
        //将页面根View作为子View，其他参数保持不变
        ViewGroup vg = new AutoSpeedFrameLayout(child.getContext(), pageObjectKey);
        vg.setLayoutParams(params);
        vg.addView(child, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        return vg;
    }

    public static View wrap(int pageObjectKey, Context context, @LayoutRes int layoutRes) {
        View view = LayoutInflater.from(context).inflate(layoutRes, null);
        return wrap(pageObjectKey, view);
    }

    private AutoSpeedFrameLayout(@NonNull Context context, int pageObjectKey) {
        super(context);
        this.pageObjectKey = pageObjectKey;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        LogUtils.logD("onMeasure");
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
//        LogUtils.logD("onLayout");
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
//        LogUtils.logD("onDraw");
        super.onDraw(canvas);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
//        LogUtils.logD("dispatchDraw");
        super.dispatchDraw(canvas);
        AutoSpeed.getInstance().onPageLoadedFinished(pageObjectKey);
    }
}
