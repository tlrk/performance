package com.example.performance_android.utils;

import android.util.Log;

import com.example.performance_android.BuildConfig;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by tlrk on 8/6/18.
 */

public class LogUtils {

    private final static String LOG_TAG = "performance";

    public static final SimpleDateFormat TIME_FORMATTER =
            new SimpleDateFormat("MM-dd HH:mm:ss.SSS", Locale.US);

    public static void logD(String msg) {
        if (BuildConfig.DEBUG) {
            Log.d(LOG_TAG, msg);
        }
    }

    public static void logWithTime(String msg, long time) {
        if(BuildConfig.DEBUG) {
            Log.d(LOG_TAG, msg + " " + CommonUtils.convertTime(time));
        }
    }

}
