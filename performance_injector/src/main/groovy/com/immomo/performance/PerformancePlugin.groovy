package com.immomo.performance

import org.gradle.api.Plugin
import org.gradle.api.Project
import com.android.build.gradle.AppExtension

public class PerformancePlugin implements Plugin<Project> {

    void apply(Project project) {
        System.out.println("------------------开始----------------------");
        System.out.println("这是我们的自定义插件!");

        def android = project.extensions.getByType(AppExtension)
        //注册一个Transform
        def classTransform = new PerformanceTransform(project);
        android.registerTransform(classTransform);

        System.out.println("------------------结束----------------------->");
    }
}