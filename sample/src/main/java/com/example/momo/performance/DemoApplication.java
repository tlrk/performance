package com.example.momo.performance;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.android.alpha.OnProjectExecuteListener;
import com.example.momo.performance.alpha.ConfigTest;
import com.facebook.device.yearclass.YearClass;
import com.tlrk.performance.AutoSpeed;
import com.tlrk.performance.PerformanceConfig;
import com.tlrk.performance.PerformanceMonitor;
import com.tlrk.performance.utils.LogUtils;

import java.util.concurrent.CountDownLatch;

/**
 * Created by momo on 8/6/18.
 */

public class DemoApplication extends Application {

    private String mGloableStr;
    private CountDownLatch mCountDownLatch = new CountDownLatch(1);
    public DemoApplication() {
        super();
    }


    @Override
    public void onCreate() {
        AutoSpeed.getInstance().init(this);
        super.onCreate();
        testYearClass();
        AppKit.init(this);
        mGloableStr = "init str";
        launch();
        try {
            mCountDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.d("StartUpConfig", "main ------> main");
    }

    public void launch() {
        ConfigTest config = new ConfigTest(this);
        config.setOnProjectExecuteListener(new OnProjectExecuteListener() {
            @Override
            public void onProjectStart() {

            }

            @Override
            public void onTaskFinish(String taskName) {

            }

            @Override
            public void onProjectFinish() {
                mCountDownLatch.countDown();
            }
        });
        config.start();
    }

    public String getGlobalStr() {
        if (Thread.currentThread() != Looper.getMainLooper().getThread()) {
            throw new IllegalStateException("must init in main thread");
        }
        if (TextUtils.isEmpty(mGloableStr)) {
            throw new IllegalStateException("global str must init");
        }
        return mGloableStr;
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
