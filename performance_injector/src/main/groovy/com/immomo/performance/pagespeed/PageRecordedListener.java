package com.tlrk.performance.pagespeed;

import com.tlrk.framework.statistics.pagespeed.bean.PageSpeedRecord;

/**
 * 获取到页面的加载时间，回调此接口
 * Created by tlrk on 9/3/18.
 */
public interface PageRecordedListener {

    void onPageRecorded(PageSpeedRecord record);
}
