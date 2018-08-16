package com.example.performance_android.pageLaunch;

/**
 * Created by tlrk on 8/16/18.
 */
public interface PageLifeCycleCallback {

    /**
     * 页面创建时候的回调
     */
    void onPageCreated();


    /**
     * 页面显示出来的回调
     */
    void onPageVisibileToUser();
}
