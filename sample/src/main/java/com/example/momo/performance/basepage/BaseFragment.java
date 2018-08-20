package com.example.momo.performance.basepage;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.performance_android.AutoSpeed;
import com.example.performance_android.AutoSpeedFrameLayout;
import com.example.performance_android.utils.CommonUtils;

/**
 * Created by momo on 8/6/18.
 */

public abstract class BaseFragment extends Fragment {


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AutoSpeed.getInstance().onPageCreate(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return AutoSpeedFrameLayout.wrap(CommonUtils.getObjectKey(this), getContentView(inflater, container, savedInstanceState));
    }

    protected abstract View getContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState);


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        AutoSpeed.getInstance().onPageDestroy(this);
    }
}
