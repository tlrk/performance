package com.example.momo.performance;

import android.app.Application;

import com.example.performance_android.PerformanceConfig;
import com.example.performance_android.PerformanceMonitor;

/**
 * Created by momo on 8/6/18.
 */

public class DemoApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        PerformanceMonitor.install(new PerformanceConfig.Builder(this)
                .setCapturePerformance(true)
                .setMonitorBlock(true)
                .build());
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        PerformanceMonitor.unInstall();
    }
}
