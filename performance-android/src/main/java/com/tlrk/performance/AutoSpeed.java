package com.tlrk.performance;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import com.tlrk.performance.utils.CommonUtils;
import com.tlrk.performance.utils.LogUtils;


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
    private void onColdStart(long coldStartTime) {
        this.coldStartTime = coldStartTime;
        LogUtils.logWithTime("onColdStart", coldStartTime);
    }


    public void init(Context context) {
        this.appContext = context;
        onColdStart(CommonUtils.getRealTime());
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

    public void onPageDestroy(Object uiObj) {
        int pageObjKey = CommonUtils.getObjectKey(uiObj);
        PageObject pageObject = activePages.get(pageObjKey);
        if (pageObject != null) {
            activePages.remove(pageObjKey);
            log(pageObject.getPageTag() + " destroyed to end track");
            pageObject = null;
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
        boolean isMainPage = "MainTabActivity".equals(pageObject.getPageTag());
        if (isMainPage) {
            long endTime = pageObject.getInitialDrawEndTime();
            log("app(Main page) cost " + (endTime - coldStartTime) + " ms");
        }
    }
}
