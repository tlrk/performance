package com.example.performance_android.appluanch;

import com.example.performance_android.model.BasicInfo;

/**
 * 冷启动时间监控的数据对象
 * Created by tlrk on 8/15/18.
 */
public class AppLaunchInfo {

    BasicInfo basicInfo;

    // 应用 application OnCreate 启动的时刻
    long appStartCreatedTime;

    // 应用 application OnCreate 结束的时刻
    long appEndCreateTime;

    // 主 activity onCreate 开始的时刻
    long mainActStartCreateTime;

    // 主 activity 显示在前台的时刻
    long mainActVisibileToUserTime;
}
