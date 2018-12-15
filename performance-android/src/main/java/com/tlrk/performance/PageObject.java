package com.tlrk.performance;

import com.tlrk.performance.utils.CommonUtils;
import com.tlrk.performance.utils.LogUtils;

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
    private boolean hasShow = false;

    public PageObject(int objectKey, String pageTag, PageShowListener pageShowListener) {
        this.objectKey = objectKey;
        this.pageTag = pageTag;
        this.pageShowListener = pageShowListener;
    }

    public void onCreate() {
        createTime = CommonUtils.getRealTime();
        LogUtils.logWithTime(pageTag + " onPageCreate ", createTime);
    }

    public void onDrawEnd() {

        if (hasShow)
            return;

        initialDrawEndTime = CommonUtils.getRealTime();
        LogUtils.logWithTime(pageTag + " onPageDrawEnd ", initialDrawEndTime);
        if (!hasShow && pageShowListener != null) {
            pageShowListener.onPageShow(objectKey);
            hasShow = true;
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

    public String getPageTag() {
        return pageTag;
    }
}
