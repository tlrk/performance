package com.example.performance_android;

import android.content.Context;

/**
 * Created by tlrk on 8/6/18.
 */

public class PerformanceConfig {

    public static final int DEFAULT_FPS_THRESHOLD = 20;

    public static final long DEFAULT_STACK_DUMP_INTERVAL = 1000L;

    private Context context;

    // 是否实时抓取性能数据
    private boolean capturePerformance = true;

    // 是否打开卡顿监测
    private boolean monitorBlock = true;

    // 帧率阈值，低于该值将被视为卡顿
    private int fpsThreshold = DEFAULT_FPS_THRESHOLD;

    // 栈 dump 频率，单位是 ms
    private long stackDumpInterval = DEFAULT_STACK_DUMP_INTERVAL;

    // 网络状况 like 2G, 3G, 4G, wifi, etc.
    private String networkType = "unknown";

    public String provideNetworkType() {
        return networkType;
    }

    public int provideFpsThreshold () {
        return fpsThreshold;
    }

    public long provideStackDumpInterval() {
        return stackDumpInterval;
    }

    public static class Builder {
        private Context context;

        // 是否实时抓取性能数据
        boolean capturePerformance = true;

        // 是否打开卡顿监测
        private boolean monitorBlock = true;

        private int fpsThreshold = DEFAULT_FPS_THRESHOLD;

        private long stackDumpInterval = DEFAULT_STACK_DUMP_INTERVAL;

        private String networkType = "unknown";

        public Builder(Context context) {
            this.context = context;
        }

        public Builder setCapturePerformance(boolean capturePerformance) {
            this.capturePerformance = capturePerformance;
            return this;
        }

        public Builder setMonitorBlock(boolean monitorBlock) {
            this.monitorBlock = monitorBlock;
            return this;
        }

        public Builder setNetworkType(String networkType) {
            this.networkType = networkType;
            return this;
        }

        public Builder setFpsThreshold(int fpsThreshold) {
            this.fpsThreshold = fpsThreshold;
            return this;
        }

        public Builder seStackDumpInterval(int stackDumpInterval) {
            this.stackDumpInterval  = stackDumpInterval;
            return this;
        }

        public PerformanceConfig build() {
            PerformanceConfig configuration = new PerformanceConfig();
            configuration.capturePerformance = capturePerformance;
            configuration.monitorBlock = monitorBlock;
            configuration.context = context;
            configuration.networkType = networkType;
            configuration.fpsThreshold = fpsThreshold;
            configuration.stackDumpInterval = stackDumpInterval;
            return configuration;
        }
    }
}
