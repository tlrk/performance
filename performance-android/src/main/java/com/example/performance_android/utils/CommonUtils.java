package com.example.performance_android.utils;

import android.os.SystemClock;

/**
 * Created by tlrk on 8/16/18.
 */
public class CommonUtils {

    public static long getRealTime() {
        return SystemClock.elapsedRealtime();
    }

    public static int getObjectKey(Object pageObj) {
        return pageObj.hashCode();
    }
}
