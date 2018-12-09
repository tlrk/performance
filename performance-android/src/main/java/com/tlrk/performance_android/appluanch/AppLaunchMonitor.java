package com.tlrk.performance_android.appluanch;

import com.tlrk.performance_android.BaseMonitor;
import com.tlrk.performance_android.utils.LogUtils;

/**
 * Created by tlrk on 8/15/18.
 */
public class AppLaunchMonitor implements BaseMonitor {

    private static final String TAG = "AppLaunchMonitor";

    public static final int APP_LAUNCH_START_APP_CREATE = 0;
    public static final int APP_LAUNCH_END_APP_CREATE = 1;
    public static final int APP_LAUNCH_START_MAIN_ACT_CREATE = 2;
    public static final int APP_LAUNCH_VISIBLE_TO_USER = 3;

    private final AppLaunchInfo mAppLaunchInfo;

    private static volatile AppLaunchMonitor INSTANCE;

    private AppLaunchMonitor() {
        mAppLaunchInfo = new AppLaunchInfo();
    }

    public static AppLaunchMonitor getInstance() {
        if (INSTANCE == null) {
            synchronized (AppLaunchMonitor.class) {
                if (INSTANCE == null) {
                    INSTANCE = new AppLaunchMonitor();
                }
            }
        }
        return INSTANCE;
    }

    @Override
    public void start() {

    }

    // todo 可以用字节码注入的方式实现自动打点
    @Override
    public void stop() {

    }

    @Override
    public void dispose() {

    }

    public void markLifeCycleMethodExecute(int happenedPlace) {
        if (mAppLaunchInfo == null) {
            throw new IllegalStateException(" app launch info not create yet");
        }
        long happenedTime = System.currentTimeMillis();
        switch (happenedPlace) {
            case APP_LAUNCH_START_APP_CREATE:
                LogUtils.logD("AppLaunchMonitor appStartCreate at " + happenedPlace);
                mAppLaunchInfo.appStartCreatedTime = happenedTime;
                break;
            case APP_LAUNCH_END_APP_CREATE:
                LogUtils.logD("AppLaunchMonitor appEndCreate at " + happenedPlace);
                mAppLaunchInfo.appEndCreateTime = happenedTime;
                break;
            case APP_LAUNCH_START_MAIN_ACT_CREATE:
                LogUtils.logD("AppLaunchMonitor appStarMainAct at " + happenedPlace);
                mAppLaunchInfo.mainActStartCreateTime = happenedTime;
                break;
            case APP_LAUNCH_VISIBLE_TO_USER:
                LogUtils.logD("AppLaunchMonitor appVisibleToUser at " + happenedPlace);
                mAppLaunchInfo.mainActVisibileToUserTime = happenedTime;
                break;
                default:
                    throw new IllegalStateException("invalid method happen place");
        }
    }

}
