package com.tlrk.performance_android;

import android.content.Context;

/**
 * Created by tlrk on 8/6/18.
 */

public class PerformanceConfig {

    static final int DEFAULT_FPS_THRESHOLD = 20;

    static final long DEFAULT_STACK_DUMP_INTERVAL = 1000L;

    static final long DEFAULT_START_SAMPLE_DELAY = 0L;

    private Context context;

    // 是否实时抓取性能数据
    private boolean capturePerformance = true;

    // 是否打开卡顿监测
    private boolean monitorBlock = true;

    // 是否打开页面启动监控
    private boolean monitorPageLaunch = true;

    // 帧率阈值，低于该值将被视为卡顿
    private int fpsThreshold = DEFAULT_FPS_THRESHOLD;

    // 栈 dump 频率，单位是 ms
    private long stackDumpInterval = DEFAULT_STACK_DUMP_INTERVAL;

    // 延迟多少 ms 后开始启动采样
    private long startSampleDelay = DEFAULT_START_SAMPLE_DELAY;;

    // 网络状况 like 2G, 3G, 4G, wifi, etc.
    private String networkType = "unknown";

    private ConfigProvider configProvider;

    public String provideNetworkType() {
        return networkType;
    }

    public int provideFpsThreshold () {
        return fpsThreshold;
    }

    public long provideStackDumpInterval() {
        return stackDumpInterval;
    }

    public long provideStartSampleDelay() {
        return startSampleDelay;
    }

    public boolean getMonitorBlockEnable() {
        return monitorBlock;
    }

    public boolean getMonitorPagelaunchEnable() {
        return monitorPageLaunch;
    }

    public ConfigProvider getConfigProvider() {
        return configProvider;
    }

    public static class Builder {
        private Context context;

        boolean capturePerformance = true;

        private boolean monitorBlock = true;

        private boolean monitorPageLaunch = true;

        private int fpsThreshold = DEFAULT_FPS_THRESHOLD;

        private long stackDumpInterval = DEFAULT_STACK_DUMP_INTERVAL;

        private long startSampleDelay = DEFAULT_START_SAMPLE_DELAY;

        private String networkType = "unknown";

        private ConfigProvider configProvider;

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

        public Builder setStackDumpInterval(int stackDumpInterval) {
            this.stackDumpInterval  = stackDumpInterval;
            return this;
        }

        public Builder setStartSampleDelay(int startSampleDelay) {
            this.startSampleDelay  = startSampleDelay;
            return this;
        }

        public Builder setMonitorPageLaunch(boolean monitorPageLaunch) {
            this.monitorPageLaunch = monitorPageLaunch;
            return this;
        }

        public Builder setConfigProvider(ConfigProvider provider) {
            this.configProvider = provider;
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
            configuration.monitorPageLaunch = monitorPageLaunch;
            configuration.configProvider = configProvider;
            configuration.startSampleDelay = startSampleDelay;
            return configuration;
        }
    }

    /**
     * 宿主 app 提供一些基本信息
     */
    public interface ConfigProvider {
        String getIMEI();
        String getUserId();
        String getDeviceId();
        String getAppVersionName();
        int getAppVersionCode();
    }
}
