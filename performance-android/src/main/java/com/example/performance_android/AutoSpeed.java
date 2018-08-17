package com.example.performance_android;

import android.app.Application;
import android.content.Context;
import android.support.annotation.LayoutRes;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import com.example.performance_android.utils.CommonUtils;
import com.example.performance_android.utils.LogUtils;


/**
 * Created by tlrk on 8/16/18.
 */
public class AutoSpeed implements PageShowListener{
    private static final AutoSpeed ourInstance = new AutoSpeed();

    private Context appContext;
    private long coldStartTime;

    private SparseArray<PageObject> activePages = new SparseArray<>();

    public static AutoSpeed getInstance() {
        return ourInstance;
    }

    private AutoSpeed() {
    }

    /**
     * 冷启动初始时间以构造函数为准，可以算入MultiDex注入的时间，比在 onCreate() 中计算更为准确。
     * @param coldStartTime
     */
    public void onColdStart(long coldStartTime) {
        this.coldStartTime = coldStartTime;
        log("onColdStart " + coldStartTime);
    }


    public void init(Application context) {
        this.appContext = context;
    }

    public void onPageCreate(Object uiObj) {
        int pageObjKey = CommonUtils.getObjectKey(uiObj);
        PageObject pageObject = activePages.get(pageObjKey);
        if (pageObject == null) {
            pageObject = new PageObject(pageObjKey, uiObj.getClass().getSimpleName(), this);
            pageObject.onCreate();
            activePages.put(pageObjKey, pageObject);
        }
    }

    public void onPageDrawEnd(int pageObjKey) {
        PageObject pageObject = activePages.get(pageObjKey);
        if (pageObject != null) {
            pageObject.onDrawEnd();
        }
    }

    public View createPageView(Object pageObject, View targetView) {
        int objectKey = CommonUtils.getObjectKey(pageObject);
        return AutoSpeedFrameLayout.wrap(objectKey, targetView);
    }

    public View createPageView(Object pageObject, @LayoutRes int layoutResID) {
        int objectKey = CommonUtils.getObjectKey(pageObject);
        Context context = (Context) pageObject;
        return AutoSpeedFrameLayout.wrap(objectKey, context, layoutResID);
    }

    public View createPageView(Object pageObject, View view, ViewGroup.LayoutParams params) {
        int objectKey = CommonUtils.getObjectKey(pageObject);
        return AutoSpeedFrameLayout.wrap(objectKey, view, params);
    }

    private void log(String msg) {
        LogUtils.logD(this.getClass().getSimpleName() + " " + msg);
    }

    @Override
    public void onPageShow(int objectKey) {
        PageObject pageObject = activePages.get(objectKey);
        if (pageObject != null) {
            long endTime = pageObject.getInitialDrawEndTime();
            log("app cost " + (endTime - coldStartTime) + " ms");
        }
    }
}