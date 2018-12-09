package com.tlrk.performance_android.core;

import android.content.Context;

import com.tlrk.performance_android.model.BlockInfo;

/**
 * 负责收集配置信息
 * Created by tlrk on 8/6/18.
 */

public class BlockContextInterceptor implements BlockInterceptor {

    @Override
    public void onBlock(Context context, BlockInfo info) {

    }

    public long provideBlockDelay() {
        return 300L;
    }
}
