package com.example.performance_android;

import com.example.performance_android.utils.CommonUtils;
import com.example.performance_android.utils.LogUtils;

/**
 * Created by tlrk on 8/16/18.
 */
public class PageObject {

    private long createTime;
    private long initialDrawEndTime = 0L;
    private long finalDrawEndTime = 0L;
    private int objectKey;
    private String pageTag;
    private PageShowListener pageShowListener;

    public PageObject(int objectKey, String pageTag, PageShowListener pageShowListener) {
        this.objectKey = objectKey;
        this.pageTag = pageTag;
        this.pageShowListener = pageShowListener;
    }

    public void onCreate() {
        createTime = CommonUtils.getRealTime();
        LogUtils.logD(pageTag + " onPageCreate " + createTime);
    }

    public void onDrawEnd() {

        initialDrawEndTime = CommonUtils.getRealTime();
        LogUtils.logD(pageTag + " onPageDrawEnd " + initialDrawEndTime);
        if (pageShowListener != null) {
            pageShowListener.onPageShow(objectKey);
        }
        LogUtils.logD("Page " + pageTag + " cost " + (initialDrawEndTime - createTime) + " ms");
    }

    public long getCreateTime() {
        return createTime;
    }

    public long getInitialDrawEndTime() {
        return initialDrawEndTime;
    }

    public long getFinalDrawEndTime() {
        return finalDrawEndTime;
    }
}
