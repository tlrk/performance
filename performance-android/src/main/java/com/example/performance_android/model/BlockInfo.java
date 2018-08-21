package com.example.performance_android.model;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by tlrk on 8/6/18.
 */

public class BlockInfo {

    private static final String TAG = "BlockInfo";

    public static final SimpleDateFormat TIME_FORMATTER =
            new SimpleDateFormat("MM-dd HH:mm:ss.SSS", Locale.CHINA);

    public static final String SEPARATOR = "\r\n";
    public static final String KV = " = ";

    BasicInfo basicInfo;

    // Block Special info
    long timeCost;
    String timeStart;
    String timeEnd;
    ArrayList<String> threadStackEntries;
    String hash; // 栈信息 hash 值
    String hitCount; // 当前栈信息命中次数
    boolean hasUploaded; // 该栈信息是否已经上传
}
