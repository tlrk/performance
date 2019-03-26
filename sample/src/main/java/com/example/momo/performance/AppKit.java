package com.example.momo.performance;

import android.app.Application;
import android.os.Looper;

public class AppKit {

    private static DemoApplication mApplication;
    public static void init(Application application) {
        if (Thread.currentThread() != Looper.getMainLooper().getThread()) {
            throw new IllegalStateException("must init in main thread");
        }
        mApplication = (DemoApplication) application;
    }

    public static DemoApplication getmApplication() {
        return mApplication;
    }
}
