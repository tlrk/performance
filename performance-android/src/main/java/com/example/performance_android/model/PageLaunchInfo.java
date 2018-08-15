package com.example.performance_android.model;

/**
 * 页面 （Activity 或者 Fragment 启动速度）
 * Created by tlrk on 8/6/18.
 */

public class PageLaunchInfo extends BasicInfo {

    BasicInfo basicInfo;

    String targetPage; // 被监控要打开的页面，为 Activity 或者 Fragment 的带包名的路径
    long costTime;
    long timestamp;
}
