package com.tlrk.performance.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by tlrk on 8/16/18.
 */
public class CommonUtils {

    public static final SimpleDateFormat TIME_FORMATTER =
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.CHINA);

    public static long getRealTime() {
        //return SystemClock.elapsedRealtime();
        return System.currentTimeMillis();
    }

    public static String convertTime(long time) {
        return TIME_FORMATTER.format(new Date(time));
    }

    public static int getObjectKey(Object pageObj) {
        return pageObj.hashCode();
    }
}
