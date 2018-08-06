package com.example.performance_android.block;

import android.annotation.TargetApi;
import android.os.Build;
import android.view.Choreographer;

import com.example.performance_android.utils.LogUtils;

import java.lang.ref.WeakReference;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by momo on 2017/8/4.
 * 帧率计算器
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class FPSMonitor {

    private static class Singleton {
        private static FPSMonitor INSTANCE = new FPSMonitor();
    }

    private FPSMonitor() {
    }

    public static FPSMonitor get() {
        return Singleton.INSTANCE;
    }

    private AtomicBoolean mIsStarted = new AtomicBoolean(false);
    private FrameCallback mFrameCallback = new FrameCallback(this);

    private FPSMonitorListener mFPSMonitorListener;

    public void setFPSMonitorListener(FPSMonitorListener l) {
        mFPSMonitorListener = l;
    }

    // 上一秒的时间值
    private long mLastTimeNanos;
    // 当前秒已经合成的帧数
    private volatile int mClockFrame;
    // 上一秒的帧率
    private volatile int FPS;

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void start() {
        if (mIsStarted.get()) {
            return;
        }
        mIsStarted.set(true);
        scheduleNextVSync();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void stop() {
        if (!mIsStarted.get()) {
            return;
        }
        mIsStarted.set(false);
        mFPSMonitorListener = null;
        Choreographer.getInstance().removeFrameCallback(mFrameCallback);
        reset();
    }

    public void reset() {
        mLastTimeNanos = 0;
        mClockFrame = 0;
        FPS = 0;
    }

    private void onSync(long frameTimeNanos) {
        if (mLastTimeNanos == 0) {
            // 检测器启动后，第一帧跳过
            mLastTimeNanos = frameTimeNanos;
            mClockFrame = 0;
        } else {
            // 累加一帧
            mClockFrame++;
            if (TimeUnit.NANOSECONDS.toMillis((frameTimeNanos - mLastTimeNanos)) >= 1000) {
                // 如果时间已经过去了1S，则计算帧率
                FPS = Math.min(60, mClockFrame);
                LogUtils.logD("pre : " + LogUtils.TIME_FORMATTER.format(mLastTimeNanos)
                        + " cur : " + LogUtils.TIME_FORMATTER.format(frameTimeNanos)
                        + " fps = " + FPS);
                if (mFPSMonitorListener != null) {
                    mFPSMonitorListener.onBlock(mLastTimeNanos, frameTimeNanos, FPS);
                }
                mLastTimeNanos = frameTimeNanos;
                mClockFrame = 0;
            }
        }

        scheduleNextVSync();
    }

    /**
     * 注册下一个垂直同步信号
     */
    private void scheduleNextVSync() {
        Choreographer.getInstance().postFrameCallback(mFrameCallback);
    }


    /**
     * 垂直同步信号回调
     */
    private static class FrameCallback implements Choreographer.FrameCallback {

        private final WeakReference<FPSMonitor> mRef;

        FrameCallback(FPSMonitor monitor) {
            this.mRef = new WeakReference<FPSMonitor>(monitor);
        }

        @Override
        public void doFrame(long frameTimeNanos) {
            FPSMonitor monitor = mRef.get();
            if (monitor == null) {
                return;
            }
            monitor.onSync(frameTimeNanos);
        }
    }

    /**
     * 获取FPS值，每1S更新
     */
    public int getFPS() {
        return FPS;
    }

}
