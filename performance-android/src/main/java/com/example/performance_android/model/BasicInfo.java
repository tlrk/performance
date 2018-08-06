package com.example.performance_android.model;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by tlrk on 8/6/18.
 */

public class BasicInfo {

    public static final SimpleDateFormat TIME_FORMATTER =
            new SimpleDateFormat("MM-dd HH:mm:ss.SSS", Locale.US);

    public static final String SEPARATOR = "\r\n";
    public static final String KV = " = ";

    // Device info
    String sImei;
    String model;
    String apiLevel;
    int cpuCoreNum;
    String network;
    String freeMemory;
    String totalMemory;

    // App info
    String uid;
    String versionName;
    int versionCode;
}
