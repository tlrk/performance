package com.example.momo.performance;

import android.app.Application;

import com.example.performance_android.PerformanceConfig;
import com.example.performance_android.PerformanceMonitor;
import com.example.performance_android.utils.LogUtils;
import com.facebook.device.yearclass.YearClass;

/**
 * Created by momo on 8/6/18.
 */

public class DemoApplication extends Application {

    public DemoApplication() {
        super();
    }

    @Override
    public void onCreate() {
        com.example.performance_android.AutoSpeed.getInstance().init(this);
        super.onCreate();
        testYearClass();
    }

    private void testYearClass() {
        int year = YearClass.get(getApplicationContext());
        LogUtils.logD("year is " + year);
    }

    //    @Override
//    public void onCreate() {
////        AutoSpeed.getInstance().init(this); Aop 方式注入
//        super.onCreate();
//        try {
//            Thread.sleep(1000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        PerformanceMonitor.install(new PerformanceConfig.Builder(this)
//                .setCapturePerformance(true)
//                .setMonitorBlock(true)
//                .setMonitorPageLaunch(true)
//                .setFpsThreshold(20)
//                .setStackDumpInterval(1000)
//                .setStartSampleDelay(0)
//                .setNetworkType("unknown")
//                .setConfigProvider(new PerformanceConfigProvider())
//                .build());
//    }

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
