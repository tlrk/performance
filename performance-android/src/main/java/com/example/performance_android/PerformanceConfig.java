package com.example.performance_android;

import android.content.Context;

/**
 * Created by tlrk on 8/6/18.
 */

public class PerformanceConfig {

    // 是否实时抓取性能数据
    private boolean capturePerformance = true;
    // 是否打开卡顿监测
    private boolean monitorBlock = true;

    private Context context;

    public static class Builder {
        // 是否实时抓取性能数据
        boolean capturePerformance = true;
        // 是否打开卡顿监测
        private boolean monitorBlock = true;

        private Context context;

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

        public PerformanceConfig build() {
            PerformanceConfig configuration = new PerformanceConfig();
            configuration.capturePerformance = capturePerformance;
            configuration.monitorBlock = monitorBlock;
            configuration.context = context;
            return configuration;
        }
    }
}
