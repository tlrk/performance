
package com.example.performance_android.sample;

import com.example.performance_android.HandlerThreadFactory;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * {@link AbstractSampler} sampler defines sampler work flow.
 */
abstract class AbstractSampler {

    private static final int DEFAULT_SAMPLE_INTERVAL = 300;
    private static final int DEFAULT_SAMPLE_DELAY = 0;

    protected AtomicBoolean mShouldSample = new AtomicBoolean(false);
    protected long mSampleInterval;
    protected long mSampleDelay;

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

    public AbstractSampler(long sampleInterval, long sampleDelay) {
        if (0 == sampleInterval) {
            sampleInterval = DEFAULT_SAMPLE_INTERVAL;
        }
        mSampleInterval = sampleInterval;
        mSampleDelay = sampleDelay;
    }

    public void start() {
        if (mShouldSample.get()) {
            return;
        }
        mShouldSample.set(true);

        HandlerThreadFactory.getTimerThreadHandler().removeCallbacks(mRunnable);
        HandlerThreadFactory.getTimerThreadHandler().postDelayed(mRunnable,
                mSampleDelay);
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
