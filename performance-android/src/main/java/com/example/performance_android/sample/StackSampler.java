package com.example.performance_android.sample;


import com.example.performance_android.model.BlockInfo;
import com.example.performance_android.utils.LogUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * Dumps thread stack.
 */
public class StackSampler extends AbstractSampler {

    private static final int DEFAULT_MAX_ENTRY_COUNT = 100;
    private static final LinkedHashMap<Long, String> sStackMap = new LinkedHashMap<>();

    private int mMaxEntryCount = DEFAULT_MAX_ENTRY_COUNT;
    private Thread mCurrentThread;

    public StackSampler(Thread thread, long sampleIntervalMillis, long sampleDelayMillis) {
        this(thread, DEFAULT_MAX_ENTRY_COUNT, sampleIntervalMillis, sampleDelayMillis);
    }

    public StackSampler(Thread thread, int maxEntryCount, long sampleIntervalMillis, long sampleDelayMillis) {
        super(sampleIntervalMillis, sampleDelayMillis);
        mCurrentThread = thread;
        mMaxEntryCount = maxEntryCount;
    }

    public ArrayList<String> getThreadStackEntries(long startTime, long endTime) {
        ArrayList<String> result = new ArrayList<>();
        synchronized (sStackMap) {
            for (Long entryTime : sStackMap.keySet()) {
                if (startTime < entryTime && entryTime < endTime) {
                    result.add(BlockInfo.TIME_FORMATTER.format(entryTime)
                            + BlockInfo.SEPARATOR
                            + BlockInfo.SEPARATOR
                            + sStackMap.get(entryTime));
                }
            }
        }
        return result;
    }

    @Override
    protected void doSample() {
        StringBuilder stringBuilder = new StringBuilder();

        for (StackTraceElement stackTraceElement : mCurrentThread.getStackTrace()) {
            stringBuilder
                    .append(stackTraceElement.toString())
                    .append(BlockInfo.SEPARATOR);
        }

        synchronized (sStackMap) {
            if (sStackMap.size() == mMaxEntryCount && mMaxEntryCount > 0) {
                sStackMap.remove(sStackMap.keySet().iterator().next());
            }
            sStackMap.put(System.nanoTime(), stringBuilder.toString());
        }
    }
}