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
                .setMonitorPageLaunch(true)
                .setFpsThreshold(20)
                .setStackDumpInterval(1000)
                .setStartSampleDelay(0)
                .setNetworkType("unknown")
                .setConfigProvider(new PerformanceConfigProvider())
                .build());
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        PerformanceMonitor.unInstall();
    }

    private class PerformanceConfigProvider implements PerformanceConfig.ConfigProvider {

        @Override
        public String getIMEI() {
            return null;
        }

        @Override
        public String getUserId() {
            return null;
        }

        @Override
        public String getDeviceId() {
            return null;
        }

        @Override
        public String getAppVersionName() {
            return null;
        }

        @Override
        public int getAppVersionCode() {
            return 0;
        }
    }
}
