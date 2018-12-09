package com.tlrk.performance.pagespeed;


import com.tlrk.framework.statistics.pagespeed.bean.PageSpeedRecord;
import com.tlrk.mdlog.MDLog;
import com.tlrk.momo.LogTag;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by tlrk on 8/31/18.
 */
public class PageSpeedUtils {

    public static final SimpleDateFormat TIME_FORMATTER =
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.CHINA);

    public static long getRealTime() {
        return System.currentTimeMillis();
    }

    public static String convertTime(long time) {
        return TIME_FORMATTER.format(new Date(time));
    }

    public static int getObjectKey(Object pageObj) {
        return pageObj.hashCode();
    }

    public static void logWithTime(String msg, long time) {
        MDLog.d(LogTag.Opm.pageSpeed, msg + " " + convertTime(time));
    }

    public static PageSpeedRecord generateRecord(final PageObject object, AutoSpeed.ConfigProvider configProvider) {
        PageSpeedRecord record = new PageSpeedRecord();
        record.uid = configProvider.getUserId();
        record.appVersionName = configProvider.getAppVersionName();
        record.appVersionCode = configProvider.getAppVersionCode();
        record.osVersion = configProvider.getOsVersion();
        record.brand = configProvider.getBrand();
        record.pageSimpleClass = object.getPageTag();
        record.timestamp = object.getCreateTime();
        record.costInMilliseconds = object.getTimeCost();
        record.isColdLaunch = false;
        return record;
    }

    public static PageSpeedRecord generateAppColdLaunchRecord(AutoSpeed.ConfigProvider configProvider, long coldStartTime) {
        PageSpeedRecord record = new PageSpeedRecord();
        record.uid = configProvider.getUserId();
        record.appVersionName = configProvider.getAppVersionName();
        record.appVersionCode = configProvider.getAppVersionCode();
        record.osVersion = configProvider.getOsVersion();
        record.brand = configProvider.getBrand();
        record.pageSimpleClass = "MomoApplication";
        record.costInMilliseconds = getRealTime() - coldStartTime;
        record.isColdLaunch = true;
        return record;
    }

}
