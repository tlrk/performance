package com.immomo.performance

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import com.dd.buildgradle.ConvertUtil
import javassist.CannotCompileException
import javassist.ClassPool
import javassist.CtClass
import javassist.CtMethod
import javassist.NotFoundException
import org.apache.commons.io.FileUtils
import org.gradle.api.Project

public class PerformanceTransform extends Transform {


    private Project mProject
    private static ClassPool classPool
    private String applicationName
    private String[] basePagesArray

    PerformanceTransform(Project project) {
        mProject = project;
    }


    @Override
    String getName() {
        return "performance_injector"
    }

    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        TransformManager.CONTENT_CLASS
    }

    @Override
    Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    @Override
    boolean isIncremental() {
        return false
    }

    private static void injectBaseActivityCode(CtClass ctBaseAct, String filePath) {
        println("inject --> " + ctBaseAct.getName() + " <-- begin")
        classPool.importPackage("android.os.Bundle")
        ctBaseAct.defrost()
        boolean onCreateDone = false
        boolean onDestroyDone = false
        boolean[] setContentViewDones = new boolean[3]
        int step = 0;

        try {
            for (CtMethod ctMethod : ctBaseAct.getDeclaredMethods()) {
                if (ctMethod.getName().contains("onCreate")) {
                    ctMethod.insertAfter("com.example.performance_android.AutoSpeed.getInstance().onPageCreate(this);")
                    onCreateDone = true
                    println(String.valueOf(++step) + ", ----> " + ctBaseAct.getName() + ".onCreate finished")
                } else if (ctMethod.getName().contains("onDestroy")) {
                    ctMethod.insertAfter("com.example.performance_android.AutoSpeed.getInstance().onPageDestroy(this);")
                    onDestroyDone = true
                    println(String.valueOf(++step) + ", ----> " + ctBaseAct.getName() + ".onDestroy finished")
                } else if (ctMethod.getName().contains("setContentView")) {

                }
            }

        } catch (CannotCompileException | NotFoundException e) {
            println("could not found onCreate in Application;   " + e.toString())
        } catch (Exception e) {
            println("inject activity method failed;   " + e.toString())
        } finally {
            if (!onCreateDone) {
                String createBody =
                        """
                        protected void onCreate(@Nullable Bundle savedInstanceState) {
                            super.onCreate(savedInstanceState);
                            com.example.performance_android.AutoSpeed.getInstance().onPageCreate(this);
                        }
                        """
                ctBaseAct.addMethod(CtMethod.make(createBody, ctBaseAct))
                onCreateDone = true
                println(String.valueOf(++step) + ", ----> " + ctBaseAct.getName() + ".onCreate finished")
            }

            if (!onDestroyDone) {
                String destroyBody =
                        """
                        protected void onDestroy() {
                            super.onDestroy();
                            com.example.performance_android.AutoSpeed.getInstance().onPageDestroy(this);
                        }
                        """
                ctBaseAct.addMethod(CtMethod.make(destroyBody.toString(), ctBaseAct))
                onDestroyDone = true
                println(String.valueOf(++step) + ", ----> " + ctBaseAct.getName() + ".onDestroy finished")
            }

            if (!onCreateDone || !onDestroyDone) {
                throw new IllegalStateException("not all methods inject complete, please check")
            }
        }
        ctBaseAct.writeFile(filePath)

        println("inject --> " + ctBaseAct.getName() + " <-- end\n")
    }


    private static void injectApplicationCode(CtClass ctClassApplication, String filePath) {
        println("inject --> " + ctClassApplication.getName() + " <-- begin")
        ctClassApplication.defrost()
        boolean success = false;
        try {
            CtMethod attachBaseContextMethod = ctClassApplication.getDeclaredMethod("onCreate", null)
            attachBaseContextMethod.insertBefore("com.example.performance_android.AutoSpeed.getInstance().init(this);")
            success  = true
        } catch (CannotCompileException | NotFoundException e) {

            println("could not found onCreate in Application;   " + e.toString())

            String createBody =
                    """
                    public void onCreate() {
                        com.example.performance_android.AutoSpeed.getInstance().init(this);
                        super.onCreate();
                    }
                    """
            ctClassApplication.addMethod(CtMethod.make(createBody, ctClassApplication))
            success = true
        } catch (Exception e) {
            println("could not create onCreate() in Application;   " + e.toString())
        } finally {
            println("1, inject ----> " + ctClassApplication.getName() + ".onCreate " + (success ? "finished" : "failed"))
        }
        ctClassApplication.writeFile(filePath)
        println("inject --> " + ctClassApplication.getName() + " <-- end\n")
    }

    private void getHostAppInfo() {
        applicationName = mProject.extensions.performanceInjectorCmd.applicationName
        if (applicationName == null || applicationName.isEmpty()) {
            throw new RuntimeException("you should set applicationName in performanceInjectorCmd")
        }
        String str = mProject.extensions.performanceInjectorCmd.basePagesWithFullName
        println(str)
        basePagesArray = str.split(",")
        if (basePagesArray == null) {
            throw new RuntimeException("you should set basePages class in performanceInjectorCmd");
        }
    }

    private boolean isApplication(CtClass ctClass) {
        try {
            if (applicationName != null && applicationName == ctClass.getName()) {
                return true
            }
        } catch (Exception e) {
            println "class not found exception class name:  " + ctClass.getName()
        }
        return false
    }

    private boolean isBaseActivity(CtClass ctClass) {
        return basePagesArray != null && basePagesArray.contains(ctClass.getName())
    }

    @Override
    void transform(Context context, Collection<TransformInput> inputs, Collection<TransformInput> referencedInputs, TransformOutputProvider outputProvider, boolean isIncremental) throws IOException, TransformException, InterruptedException {
        System.out.println("----------------进入transform了--------------")
        getHostAppInfo()
        classPool = new ClassPool()
        mProject.android.bootClasspath.each {
            classPool.appendClassPath((String) it.absolutePath)
        }
        classPool.importPackage("android.support.v7.app.AppCompatActivity")

        def box = ConvertUtil.toCtClasses(inputs, classPool)

        CtClass appCtClass
        List<CtClass> activityCtClasses = new ArrayList<>();

        for (CtClass ctClass : box) {
            if (isApplication(ctClass)) {
                appCtClass = ctClass;
                println("app class is " + ctClass.getName())
            }
            if (isBaseActivity(ctClass)) {
                activityCtClasses.add(ctClass)
                println("base page class is " + ctClass.getName())
            }
        }

        //遍历input
        inputs.each { TransformInput input ->
            //遍历文件夹
            input.directoryInputs.each { DirectoryInput directoryInput ->
                //注入代码
                System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>")
                System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>")

                String fileName = directoryInput.file.absolutePath
                File dir = new File(fileName)
                dir.eachFileRecurse { File file ->

                    String filePath = file.absolutePath
                    String classNameTemp = filePath.replace(fileName, "")
                            .replace("\\", ".")
                            .replace("/", ".")
                    if (classNameTemp.endsWith(".class")) {
                        String className = classNameTemp.substring(1, classNameTemp.length() - 6)
                        if (className == applicationName) {
                            injectApplicationCode(appCtClass, fileName)
                        }

                        for (CtClass actClass : activityCtClasses) {
                            if (className == actClass.getName()) {
                                injectBaseActivityCode(actClass, fileName)
                            }
                        }
                    }
                }



                // 获取output目录
                def dest = outputProvider.getContentLocation(directoryInput.name,
                        directoryInput.contentTypes, directoryInput.scopes, Format.DIRECTORY)

                // 将input的目录复制到output指定目录
                FileUtils.copyDirectory(directoryInput.file, dest)
            }

            ////遍历jar文件 对jar不操作，但是要输出到out路径
            input.jarInputs.each { JarInput jarInput ->
                // 重命名输出文件（同目录copyFile会冲突）
                def jarName = jarInput.name
                println("jar = " + jarInput.file.getAbsolutePath())
                def md5Name = InjectUtils.getMD5(jarInput.file.getAbsolutePath())
                if (jarName.endsWith(".jar")) {
                    jarName = jarName.substring(0, jarName.length() - 4)
                }
                def dest = outputProvider.getContentLocation(jarName + md5Name, jarInput.contentTypes, jarInput.scopes, Format.JAR)
                FileUtils.copyFile(jarInput.file, dest)
            }
        }
        System.out.println("--------------结束transform了----------------")
    }
}
