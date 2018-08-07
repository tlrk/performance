package com.example.performance_android.core;

import android.os.Looper;

import com.example.performance_android.sample.CpuSampler;
import com.example.performance_android.sample.StackSampler;
import com.example.performance_android.utils.LogUtils;

/**
 * Created by tlrk on 8/6/18.
 */

public class BlockMonitorCore implements FPSMonitorListener {

    private static volatile BlockMonitorCore INSTANCE;

    public BlockMonitorCore() {
        stackSampler = new StackSampler(Looper.getMainLooper().getThread(), SAMPLE_INTERVAL, SAMPLE_DELAY);
        cpuSampler = new CpuSampler(SAMPLE_INTERVAL, SAMPLE_DELAY);
    }

    StackSampler stackSampler;
    CpuSampler cpuSampler;
    public static long SAMPLE_INTERVAL = 300L;
    public static long SAMPLE_DELAY = 0L;

    public static BlockMonitorCore getInstance() {
        if (INSTANCE == null) {
            synchronized (BlockMonitorCore.class) {
                if (INSTANCE == null) {
                    INSTANCE = new BlockMonitorCore();
                }
            }
        }
        return INSTANCE;
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
