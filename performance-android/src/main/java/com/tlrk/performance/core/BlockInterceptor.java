package com.tlrk.performance.core;

import android.content.Context;

import com.tlrk.performance.model.BlockInfo;

/**
 * Created by tlrk on 8/6/18.
 */

public interface BlockInterceptor {

    void onBlock(Context context, BlockInfo info);
}
