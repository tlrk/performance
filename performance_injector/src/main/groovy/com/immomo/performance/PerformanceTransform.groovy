package com.tlrk.performance

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import com.dd.buildgradle.ConvertUtil
import javassist.CannotCompileException
import javassist.ClassPool
import javassist.CtClass
import javassist.CtMethod
import javassist.NotFoundException
import javassist.expr.ExprEditor
import javassist.expr.MethodCall
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

    private static void recordStep(int step, CtMethod injectMethod, boolean  success, Exception e) {
        println("\t" + step + ", ----> " + injectMethod.getDeclaringClass().getName() + "." + injectMethod.getName()
                + " " + injectMethod.getSignature() + (success ? " finished" : " failed:" + e.getCause()))
    }

    private static void recordCreateStep(int step, CtClass injectClass, String methodName) {
        println("\t" + step + ", ----> " + injectClass.getName() + "." + methodName
                + " generate finished")
    }

    private static void injectBaseActivityCode(CtClass ctBaseAct, String filePath) {
        println("inject --> " + ctBaseAct.getName() + " <-- begin")
        classPool.importPackage("android.os.Bundle")
        classPool.importPackage("android.view.View")
        ctBaseAct.defrost()
        boolean onCreateDone = false
        boolean onDestroyDone = false
        boolean[] setContentViewDones = new boolean[3]
        int step = 0

        try {
            for (CtMethod ctMethod : ctBaseAct.getDeclaredMethods()) {
                if (ctMethod.getName() == "onCreate") {
                    ctMethod.insertAfter("com.example.performance_android.AutoSpeed.getInstance().onPageCreate(this);")
                    onCreateDone = true
                    recordStep(++step, ctMethod, true, null)
                } else if (ctMethod.getName() == "onDestroy") {
                    ctMethod.insertAfter("com.example.performance_android.AutoSpeed.getInstance().onPageDestroy(this);")
                    onDestroyDone = true
                    recordStep(++step, ctMethod, true, null)
                } else if (ctMethod.getName() == "setContentView") {
                    switch (ctMethod.getSignature()) {
                        //setContentView(View view) 和 setContentView(int layoutResID) 都只有一个参数
                        case "(Landroid/view/View;)V":
                        case "(I)V":
                            boolean isParamView = ctMethod.getSignature() == "(Landroid/view/View;)V"
                            try {
                                ctMethod.instrument(
                                        new ExprEditor() {
                                            void edit(MethodCall m) throws CannotCompileException {
                                                if (m.isSuper()) {
                                                    if (isParamView) {
                                                        m.replace("{ \$1 = com.example.performance_android.AutoSpeed.getInstance().createPageView(this, \$1); \$_ = \$proceed(\$\$);}")
                                                    } else {
                                                        m.replace("{ android.view.View v = com.example.performance_android.AutoSpeed.getInstance().createPageView(this, \$1); this.setContentView(v);}")
                                                    }
                                                }
                                            }
                                        })
                                setContentViewDones[isParamView ? 0 : 1] = true
                                recordStep(++step, ctMethod, true, null)
                            } catch (Exception e) {
                                setContentViewDones[isParamView ? 0 : 1] = false
                                recordStep(++step, ctMethod, false, e)
                            }
                            break
                        case "(Landroid/view/View;Landroid/view/ViewGroup\$LayoutParams;)V":
                            try {
                                ctMethod.instrument(
                                        new ExprEditor() {
                                            void edit(MethodCall m) throws CannotCompileException {
                                                if (m.isSuper()) {
                                                    m.replace("{ android.view.View v = com.example.performance_android.AutoSpeed.getInstance().createPageView(this, \$1, \$2); this.setContentView(v);}")
                                                }
                                            }
                                        })
                                setContentViewDones[2] = true
                                recordStep(++step, ctMethod, true, null)
                            } catch (Exception e) {
                                setContentViewDones[2] = true
                                recordStep(++step, ctMethod, false, e)
                            }
                            break
                    }
                }
            }

        } catch (CannotCompileException | NotFoundException e) {
            println("inject activity method failed;   " + e.toString())
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
                recordCreateStep(++step, ctBaseAct, "onCreate(@Nullable Bundle savedInstanceState)")
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
                recordCreateStep(++step, ctBaseAct, "void onDestroy()")
            }

            if (!setContentViewDones[0]) {
                String setContentView1 =
                        """
                        public void setContentView(View view) {
                            super.setContentView(com.example.performance_android.AutoSpeed.getInstance().createPageView(this, view));
                        }
                        """
                ctBaseAct.addMethod(CtMethod.make(setContentView1.toString(), ctBaseAct))
                setContentViewDones[0] = true
                recordCreateStep(++step, ctBaseAct, "setContentView(View view)")
            }

            if (!setContentViewDones[1]) {
                String setContentView2 =
                        """
                        public void setContentView(int layoutResID) {
                            super.setContentView(com.example.performance_android.AutoSpeed.getInstance().createPageView(this, layoutResID));
                        }
                        """
                ctBaseAct.addMethod(CtMethod.make(setContentView2.toString(), ctBaseAct))
                setContentViewDones[1] = true
                recordCreateStep(++step, ctBaseAct, "setContentView(int layoutResID)")
            }

            if (!setContentViewDones[2]) {
                String setContentView3 =
                        """
                        public void setContentView(View view, ViewGroup.LayoutParams params) {
                            super.setContentView(com.example.performance_android.AutoSpeed.getInstance().createPageView(this, view, params));
                        }
                        """
                ctBaseAct.addMethod(CtMethod.make(setContentView3.toString(), ctBaseAct))
                setContentViewDones[2] = true
                recordCreateStep(++step, ctBaseAct, "setContentView(View view, ViewGroup.LayoutParams params)")
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
                            if (className.contains("Activity")) {
                                println(" set class = " + actClass.getName() + " curclass = " + className)
                            }
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
