package com.tlrk.performance_android.core;

import android.content.Context;

import com.tlrk.performance_android.model.BlockInfo;

/**
 * Created by tlrk on 8/6/18.
 */

public interface BlockInterceptor {

    void onBlock(Context context, BlockInfo info);
}
