package com.immomo.performance

import javassist.CtClass
import javassist.CtMethod;
import org.gradle.api.Project;

import java.io.File;

import javassist.ClassPool;

/**
 * Created by tlrk on 8/21/18.
 */
public class PerformanceInjects {

    private final static ClassPool classPool = ClassPool.getDefault();

    public static void inject(String path, Project project) throws Exception{
        classPool.appendClassPath(path);
        //project.android.bootClasspath 加入android.jar，不然找不到android相关的所有类
        classPool.appendClassPath(project.android.bootClasspath[0].toString());
        classPool.importPackage("android.os.Bundle");

        File dir = new File(path);

        if (dir.isDirectory()) {
            dir.eachFileRecurse {
                File file ->
                    String filePath = file.absolutePath
                    println("filePath = " + filePath)
                    if (file.getName().equals("MainTabActivity.class")) {

                        //获取MainActivity.class
                        CtClass ctClass = classPool.getCtClass("com.example.momo.performance.page.MainTabActivity");
                        println("ctClass = " + ctClass)
                        //解冻
                        if (ctClass.isFrozen())
                            ctClass.defrost()

                        //获取到OnCreate方法
                        CtMethod ctMethod = ctClass.getDeclaredMethod("onCreate")

                        println("方法名 = " + ctMethod)

                        String insetBeforeStr = """ android.widget.Toast.makeText(this,"我是被插入的Toast代码~!!",android.widget.Toast.LENGTH_SHORT).show();
                                                """
                        //在方法开头插入代码
                        ctMethod.insertBefore(insetBeforeStr);
                        ctClass.writeFile(path)
                        ctClass.detach()//释放
                    }
            }
        }
    }
}
