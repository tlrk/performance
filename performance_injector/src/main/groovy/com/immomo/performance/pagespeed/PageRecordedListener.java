package com.immomo.performance.pagespeed;

import com.immomo.framework.statistics.pagespeed.bean.PageSpeedRecord;

/**
 * 获取到页面的加载时间，回调此接口
 * Created by tlrk on 9/3/18.
 */
public interface PageRecordedListener {

    void onPageRecorded(PageSpeedRecord record);
}
