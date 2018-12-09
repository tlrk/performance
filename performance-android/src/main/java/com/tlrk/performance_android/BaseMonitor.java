package com.tlrk.performance_android;

/**
 * Created by tlrk on 8/15/18.
 */
public interface BaseMonitor {

    void start();
    void stop();

    /**
     * 资源释放工作
     */
    void dispose();
}
