package com.immomo.performance.pagespeed;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import com.immomo.framework.statistics.pagespeed.bean.PageSpeedRecord;


/**
 * Created by tlrk on 8/16/18.
 */
public class AutoSpeed implements PageLoadedListener {

    private static final AutoSpeed ourInstance = new AutoSpeed();

    private boolean enable = true;
    private Context appContext;
    private long coldStartTime;
    private PageRecordedListener pageRecordListener;
    private ConfigProvider configProvider;

    private SparseArray<PageObject> activePages = new SparseArray<>();

    public static AutoSpeed getInstance() {
        return ourInstance;
    }

    private AutoSpeed() {
    }

    public void init(Context context, ConfigProvider provider) {
        this.appContext = context;
        this.configProvider = provider;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    /**
     * 冷启动初始时间以构造函数为准，可以算入MultiDex注入的时间，
     * 比在 onCreate() 中计算更为准确。
     */
    public void onColdStart() {
        if (enable) {
            this.coldStartTime = PageSpeedUtils.getRealTime();
            //PageSpeedUtils.logWithTime("onColdStart", coldStartTime);
        }
    }

    public void onPageCreate(Object uiObj) {
        if (enable) {
            int pageObjKey = PageSpeedUtils.getObjectKey(uiObj);
            PageObject pageObject = activePages.get(pageObjKey);
            if (pageObject == null) {
                pageObject = new PageObject(uiObj.getClass().getSimpleName(), this);
                pageObject.onCreate();
                activePages.put(pageObjKey, pageObject);
            }
        }
    }

    public void onPageLoadedFinished(int pageObjKey) {
        if (enable) {
            PageObject pageObject = activePages.get(pageObjKey);
            if (pageObject != null) {
                pageObject.onPageLoadFinished();
            }
        }
    }

    public void onPageDestroy(Object uiObj) {
        if (enable) {
            int pageObjKey = PageSpeedUtils.getObjectKey(uiObj);
            PageObject pageObject = activePages.get(pageObjKey);
            if (pageObject != null) {
                activePages.remove(pageObjKey);
                pageObject.dispose();
                //MDLog.d(LogTag.Opm.pageSpeed, pageObject.getPageTag() + " destroyed to end track");
                pageObject = null;
            }
        }
    }

    public void setPageRecordListener(PageRecordedListener l) {
        pageRecordListener = l;
    }

    public View createPageView(Object pageObject, View targetView) {
        int objectKey = PageSpeedUtils.getObjectKey(pageObject);
        return AutoSpeedFrameLayout.wrap(objectKey, targetView);
    }

    public View createPageView(Object pageObject, @LayoutRes int layoutResID) {
        int objectKey = PageSpeedUtils.getObjectKey(pageObject);
        Context context = (Context) pageObject;
        return AutoSpeedFrameLayout.wrap(objectKey, context, layoutResID);
    }

    public View createPageView(Object pageObject, View view, ViewGroup.LayoutParams params) {
        int objectKey = PageSpeedUtils.getObjectKey(pageObject);
        return AutoSpeedFrameLayout.wrap(objectKey, view, params);
    }


    @Override
    public void onPageLoaded(PageObject pageObject) {

        if (pageRecordListener != null && isValidRecord(pageObject)) {
            PageSpeedRecord record = PageSpeedUtils.generateRecord(pageObject, configProvider);
            pageRecordListener.onPageRecorded(record);

            if (pageObject.getPageTag().equals(configProvider.getMainTabActName())) {
                PageSpeedRecord appLaunchRecord = PageSpeedUtils.generateAppColdLaunchRecord(configProvider, coldStartTime);
                appLaunchRecord.timestamp = coldStartTime;
                pageRecordListener.onPageRecorded(appLaunchRecord);
            }
        }
    }

    private boolean isValidRecord(PageObject object) {
        return object != null && object.getTimeCost() < 5 * 1000;
    }


    /**
     * 宿主 app 提供一些基本信息
     */
    public interface ConfigProvider {
        String getUserId();
        String getAppVersionName();
        int getAppVersionCode();
        String getOsVersion();
        String getMainTabActName();
        String getBrand();
    }
}
