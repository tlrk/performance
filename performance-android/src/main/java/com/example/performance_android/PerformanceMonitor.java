package com.example.performance_android;

import android.os.Looper;
import android.support.annotation.UiThread;

import com.example.performance_android.core.BlockMonitorCore;
import com.example.performance_android.core.FPSMonitor;

/**
 * Created by tlrk on 8/6/18.
 */

public class PerformanceMonitor {

    private static volatile PerformanceMonitor INSTANCE;
    private PerformanceConfig mConfiguration;
    private volatile boolean mIsStarted = false;

    private BlockMonitorCore mBlockMonitorCore;

    private PerformanceMonitor(PerformanceConfig config) {
        mConfiguration = config;
    }

    public static PerformanceConfig getConfiguration() {
        return INSTANCE != null ? INSTANCE.mConfiguration : null;
    }

    @UiThread
    public static void install(PerformanceConfig config) {
        if (config == null) {
            throw new RuntimeException("config can not be null");
        }

        if (Looper.myLooper() != Looper.getMainLooper()) {
            throw new RuntimeException("perform module must install by main thread");
        }

        if (INSTANCE == null) {
            synchronized (PerformanceMonitor.class) {
                if (INSTANCE == null) {
                    INSTANCE = new PerformanceMonitor(config);
                    INSTANCE.start();
                }
            }
        }
    }

    @UiThread
    public static void unInstall() {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            throw new RuntimeException("perform module must unInstall by main thread");
        }
        if (INSTANCE == null) {
            return;
        }
        INSTANCE.stop();
        INSTANCE = null;
    }

    private void start() {
        if (mIsStarted) {
            return;
        }
        mIsStarted = true;

        FPSMonitor.get().setFPSMonitorListener(new BlockMonitorCore(mConfiguration));
        FPSMonitor.get().start();
    }

    private void stop() {
        if (!mIsStarted) {
            return;
        }
        mIsStarted = false;
        FPSMonitor.get().setFPSMonitorListener(null);
        FPSMonitor.get().stop();
    }
}
