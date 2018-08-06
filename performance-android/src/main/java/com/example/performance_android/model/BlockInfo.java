package com.example.performance_android.model;

import java.util.ArrayList;

/**
 * Created by tlrk on 8/6/18.
 */

public class BlockInfo extends BasicInfo {

    private static final String TAG = "BlockInfo";

    // Block Special info
    long timeCost;
    String timeStart;
    String timeEnd;
    ArrayList<String> threadStackEntries;
    String hash; // 栈信息 hash 值
    String hitCount; // 当前栈信息命中次数
    boolean hasUploaded; // 该栈信息是否已经上传
}
