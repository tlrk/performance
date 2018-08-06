package com.example.performance_android.block;

/**
 * Created by tlrk on 8/6/18.
 */

public interface FPSMonitorListener {

    /**
     * @param previousFrameNS 上一帧的时间
     * @param currentFrameNS 下一帧的时间
     * @param fps 帧率
     */
    void onBlock(long previousFrameNS, long currentFrameNS, int fps);
}
