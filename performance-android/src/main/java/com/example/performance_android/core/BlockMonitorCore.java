package com.example.performance_android.core;

import android.os.Looper;

import com.example.performance_android.PerformanceConfig;
import com.example.performance_android.sample.CpuSampler;
import com.example.performance_android.sample.StackSampler;
import com.example.performance_android.utils.LogUtils;

/**
 * Created by tlrk on 8/6/18.
 */

public class BlockMonitorCore implements FPSMonitorListener {

    private PerformanceConfig mPerformanceConfig;

    private StackSampler stackSampler;
    private CpuSampler cpuSampler;

    public BlockMonitorCore(PerformanceConfig config) {
        mPerformanceConfig = config;
        stackSampler = new StackSampler(Looper.getMainLooper().getThread(), config.provideStackDumpInterval(), config.provideStartSampleDelay());
        cpuSampler = new CpuSampler(config.provideStackDumpInterval(), config.provideStartSampleDelay());
    }

    @Override
    public void onStartMonitor() {
//        LogUtils.logD(" BlockMonitorCore onStartMonitor thread = " + Thread.currentThread().toString());
        if (stackSampler != null) {
            stackSampler.start();
        }
//        if (cpuSampler != null) {
//            cpuSampler.start();
//        }
    }

    @Override
    public void onBlock(long previousFrameNS, long currentFrameNS, int fps) {
        LogUtils.logD("oops, blocked happened, stack : \n" + stackSampler.getThreadStackEntries(previousFrameNS, currentFrameNS));
    }

    @Override
    public void onEndMonitor() {
//        LogUtils.logD(" BlockMonitorCore onEndMonitor thread = " + Thread.currentThread().toString());
        if (stackSampler != null) {
            stackSampler.stop();
        }

//        if (cpuSampler != null) {
//            cpuSampler.stop();
//        }
    }
}
