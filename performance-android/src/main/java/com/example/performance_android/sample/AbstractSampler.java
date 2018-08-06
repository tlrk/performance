
package com.example.performance_android.sample;

import com.example.performance_android.HandlerThreadFactory;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * {@link AbstractSampler} sampler defines sampler work flow.
 */
abstract class AbstractSampler {

    private static final int DEFAULT_SAMPLE_INTERVAL = 300;

    protected AtomicBoolean mShouldSample = new AtomicBoolean(false);
    protected long mSampleInterval;

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            doSample();

            if (mShouldSample.get()) {
                HandlerThreadFactory.getTimerThreadHandler()
                        .postDelayed(mRunnable, mSampleInterval);
            }
        }
    };

    public AbstractSampler(long sampleInterval) {
        if (0 == sampleInterval) {
            sampleInterval = DEFAULT_SAMPLE_INTERVAL;
        }
        mSampleInterval = sampleInterval;
    }

    public void start() {
        if (mShouldSample.get()) {
            return;
        }
        mShouldSample.set(true);

        // todo 配置的定义
        HandlerThreadFactory.getTimerThreadHandler().removeCallbacks(mRunnable);
//        HandlerThreadFactory.getTimerThreadHandler().postDelayed(mRunnable,
//                BlockCanaryInternals.getInstance().getSampleDelay());
        HandlerThreadFactory.getTimerThreadHandler().postDelayed(mRunnable,
                300L);
    }

    public void stop() {
        if (!mShouldSample.get()) {
            return;
        }
        mShouldSample.set(false);
        HandlerThreadFactory.getTimerThreadHandler().removeCallbacks(mRunnable);
    }

    abstract void doSample();
}
