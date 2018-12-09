package com.immomo.performance.pagespeed;


/**
 * Created by tlrk on 8/16/18.
 */
public class PageObject {

    private long createTime;
    private long initialDrawEndTime = 0L;
    private String pageTag;
    private PageLoadedListener pageLoadedListener;
    private boolean hasShow = false;

    public PageObject(String pageTag, PageLoadedListener pageLoadedListener) {
        this.pageTag = pageTag;
        this.pageLoadedListener = pageLoadedListener;
    }

    public void onCreate() {
        createTime = PageSpeedUtils.getRealTime();
        //PageSpeedUtils.logWithTime(pageTag + " onPageCreate ", createTime);
    }

    public void onPageLoadFinished() {

        if (hasShow)
            return;

        initialDrawEndTime = PageSpeedUtils.getRealTime();
        //PageSpeedUtils.logWithTime(pageTag + " onPageLoadFinished ", initialDrawEndTime);
        if (!hasShow && pageLoadedListener != null) {
            pageLoadedListener.onPageLoaded(this);
            hasShow = true;
        }
    }

    public long getCreateTime() {
        return createTime;
    }

    public long getTimeCost() {
        return initialDrawEndTime - createTime;
    }

    public String getPageTag() {
        return pageTag;
    }

    public void dispose() {
        pageLoadedListener = null;
    }
}
