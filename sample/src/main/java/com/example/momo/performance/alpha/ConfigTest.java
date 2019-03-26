/*
 * Copyright 2018 Alibaba Group.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.momo.performance.alpha;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.alibaba.android.alpha.AlphaManager;
import com.alibaba.android.alpha.ITaskCreator;
import com.alibaba.android.alpha.OnGetMonitorRecordCallback;
import com.alibaba.android.alpha.OnProjectExecuteListener;
import com.alibaba.android.alpha.Project;
import com.alibaba.android.alpha.Task;
import com.example.momo.performance.AppKit;

import java.util.Map;

/**
 * Created by zhangshuliang.zsl on 15/8/24.
 */

public class ConfigTest {
    private static final String TAG = "StartUpConfig";
    private Application mApp;

    private OnProjectExecuteListener mOnProjectExecuteListener;

    public ConfigTest(Application context) {
        mApp = context;
    }

    public void start() {
        config(mApp);
        MyLog.e("==ALPHA==", "start -->" + System.currentTimeMillis());
        AlphaManager.getInstance(mApp).start();
    }

    public void setOnProjectExecuteListener(OnProjectExecuteListener listener) {
        mOnProjectExecuteListener = listener;
    }


    private void config(Application application) {


        Project.Builder builder = new Project.Builder().withTaskCreator(new MyTaskCreator(application));
        builder.add(TASK_A);
        builder.add(TASK_B).after(TASK_A);
        builder.add(TASK_C).after(TASK_A);
        builder.add(TASK_D).after(TASK_B, TASK_C);
//        builder.add(ContextLikeTask).after(TASK_A);
        builder.setProjectName("innerGroup");

        builder.setOnProjectExecuteListener(new OnProjectExecuteListener() {
            @Override
            public void onProjectStart() {
                MyLog.d(TAG,"project start");
            }

            @Override
            public void onTaskFinish(String taskName) {
                MyLog.d(TAG,"project task finish: %s", taskName);
            }

            @Override
            public void onProjectFinish() {
                MyLog.d(TAG,"project finish.");
            }
        });

        builder.setOnGetMonitorRecordCallback(new OnGetMonitorRecordCallback() {
            @Override
            public void onGetTaskExecuteRecord(Map<String, Long> result) {
                MyLog.d(TAG,"monitor result: %s", result);
            }

            @Override
            public void onGetProjectExecuteTime(long costTime) {
                MyLog.d(TAG,"monitor time: %s", costTime);
            }
        });

        Project group = builder.create();

        group.addOnTaskFinishListener(new Task.OnTaskFinishListener() {
            @Override
            public void onTaskFinish(String taskName) {
                MyLog.d(TAG,"task group finish");
            }
        });

//        builder.add(TASK_E);
//        builder.add(TASK_E).after(group);
//        builder.add(group).after(TASK_E);
        builder.add(group);
        builder.setOnGetMonitorRecordCallback(new OnGetMonitorRecordCallback() {
            @Override
            public void onGetTaskExecuteRecord(Map<String, Long> result) {
                MyLog.d(TAG,"monitor result: %s", result);
            }

            @Override
            public void onGetProjectExecuteTime(long costTime) {
                MyLog.d(TAG,"monitor time: %s", costTime);
            }
        });

        if (mOnProjectExecuteListener != null) {
            builder.setOnProjectExecuteListener(mOnProjectExecuteListener);
        }

        AlphaManager.getInstance(mApp).addProject(builder.create());

//        try {
//            AlphaManager.getInstance(mContext).addProjectsViaFile(mContext.getAssets().open("tasklist.xml"));
//        } catch (Exception e) {
//            AlphaLog.w(e);
//        }

    }

    private static final String TASK_A = "TaskA";
    private static final String TASK_B = "TaskB";
    private static final String TASK_C = "TaskC";
    private static final String TASK_D = "TaskD";
    private static final String TASK_E = "TaskE";
    private static final String TASK_F = "TaskF";
    private static final String TASK_G = "TaskG";
    private static final String ContextLikeTask = "ContextLikeTask";

    public static class MyTaskCreator implements ITaskCreator {

        private Application mApplication;
        MyTaskCreator(Application application) {
            mApplication = application;
        }

        @Override
        public Task createTask(String taskName) {
            Log.d("==ALPHA==", taskName);
            switch (taskName) {
                case TASK_A:
                    return new TaskA();
                case TASK_B:
                    return new TaskB();
                case TASK_C:
                    return new TaskC();
                case TASK_D:
                    return new TaskD();
                case TASK_E:
                    return new TaskE();
                case TASK_F:
                    return new TaskF();
                case TASK_G:
                    return new TaskG();
                case ContextLikeTask:
                    return new ContextLikeTask(ContextLikeTask, true, mApplication);
            }

            return null;
        }
    }

    public static class ContextLikeTask extends AppTask {

        public ContextLikeTask(String name, boolean isInUiThread, Application application) {
            super(name, isInUiThread, application);
            this.mApplication = application;
        }

        @Override
        public void run() {
            MyLog.d(TAG, "run task ContextLikeTask in " + Thread.currentThread().getName());
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            AppKit.init(mApplication);
            MyLog.d(TAG, "run task ContextLikeTask finished");
        }
    }

    public static class AppTask extends Task {

        protected Application mApplication;
        public AppTask(String name, Application application) {
            super(name);
            this.mApplication = application;
        }

        public AppTask(String name, int threadPriority, Application application) {
            super(name, threadPriority);
            this.mApplication = application;
        }

        public AppTask(String name, boolean isInUiThread, Application application) {
            super(name, isInUiThread);
            this.mApplication = application;
        }

        @Override
        public void run() {

        }
    }

    public static class TaskA extends Task {
        public TaskA() {
            super(TASK_A);
        }

        @Override
        public void run() {
            MyLog.d(TAG, "run task A in " + Thread.currentThread().getName());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            MyLog.d(TAG, "run task A finished");

        }
    }

    public static class TaskB extends Task {
        public TaskB() {
            super(TASK_B);
            setExecutePriority(1);
        }

        @Override
        public void run() {
            MyLog.d(TAG, "run task B in " + Thread.currentThread().getName());
            try {
                Thread.sleep(2000);
            } catch (Exception e) {
                e.printStackTrace();
            }
            MyLog.d(TAG, "run task B finished");
        }
    }


    public static class TaskC extends Task {
        public TaskC() {
            super(TASK_C);
            setExecutePriority(2);
        }

        @Override
        public void run() {
            MyLog.d(TAG, "run task C in " + Thread.currentThread().getName());
            try {
                Thread.sleep(2500);
            } catch (Exception e) {
                e.printStackTrace();
            }
            MyLog.d(TAG, "run task C finished");
        }
    }


    public static class TaskD extends Task {
        public TaskD() {
            super(TASK_D);
        }

        @Override
        public void run() {
            MyLog.d(TAG, "run task D in " + Thread.currentThread().getName());
            try {
                Thread.sleep(100);
            } catch (Exception e) {
                e.printStackTrace();
            }
            MyLog.d(TAG, "run task D finished");
        }
    }

    public static class TaskE extends Task {
        public TaskE() {
            super(TASK_E, true);
        }

        @Override
        public void run() {
            MyLog.d(TAG, "run task E in " + Thread.currentThread().getName());
        }
    }

    public static class TaskF extends Task {
        public TaskF() {
            super(TASK_F);
        }

        @Override
        public void run() {
            MyLog.d(TAG, "run task F in " + Thread.currentThread().getName());
        }
    }

    public static class TaskG extends Task {
        public TaskG() {
            super(TASK_G);
        }

        @Override
        public void run() {
            try {
                Thread.sleep(2000);
            } catch (Exception e) {
                e.printStackTrace();
            }

            MyLog.d(TAG, "run task G in " + Thread.currentThread().getName());
        }
    }



}
