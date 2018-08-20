package com.example.momo.performance.basepage;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.performance_android.pageLaunch.PageLifeCycleCallback;

/**
 * Created by momo on 8/6/18.
 */

public class BaseFragment extends Fragment {

    PageLifeCycleCallback mPageLifeCycleCallback;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (mPageLifeCycleCallback != null) {
            mPageLifeCycleCallback.onPageCreated();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mPageLifeCycleCallback != null) {
            mPageLifeCycleCallback.onPageVisibileToUser();
        }
    }
}
