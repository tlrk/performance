package com.tlrk.performance_android.core;

import android.support.annotation.UiThread;
import android.support.annotation.WorkerThread;

/**
 * Created by tlrk on 8/6/18.
 */

public interface FPSMonitorListener {

    @UiThread
    void onStartMonitor();

    /**
     * @param previousFrameNS 上一帧的时间
     * @param currentFrameNS 下一帧的时间
     * @param fps 帧率
     */
    @WorkerThread
    void onBlock(long previousFrameNS, long currentFrameNS, int fps);

    @UiThread
    void onEndMonitor();
}
