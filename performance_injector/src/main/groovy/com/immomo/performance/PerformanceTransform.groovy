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
        println("injectBaseActivityCode begin")
        classPool.importPackage("android.os.Bundle")
        ctBaseAct.defrost()

        try {
            for (CtMethod ctMethod : ctBaseAct.getDeclaredMethods()) {
                println("---> " + ctMethod.getName())
                if (ctMethod.getName().contains("onCreate")) {
                    ctMethod.insertAfter(getActOnCreateContent())
                }
            }

        } catch (CannotCompileException | NotFoundException e) {

            println("could not found onCreate in Application;   " + e.toString())

            StringBuilder methodBody = new StringBuilder()
            methodBody.append("protected void onCreate() {")
            methodBody.append("super.onCreate(Bundle savedInstanceState);")
            methodBody.append(getActOnCreateContent())
            methodBody.append("}")
            ctBaseAct.addMethod(CtMethod.make(methodBody.toString(), ctBaseAct))
        } catch (Exception e) {
            println("could not create onCreate(Bundle savedInstanceState) in Application;   " + e.toString())
        }
        ctBaseAct.writeFile(filePath)

        println("injectBaseActivityCode success ")
    }


    private static void injectApplicationCode(CtClass ctClassApplication, String filePath) {
        println("injectApplicationCode begin")
        ctClassApplication.defrost()
        try {
            CtMethod attachBaseContextMethod = ctClassApplication.getDeclaredMethod("onCreate", null)
            attachBaseContextMethod.insertBefore(getAppInjectContent())
        } catch (CannotCompileException | NotFoundException e) {

            println("could not found onCreate in Application;   " + e.toString())

            StringBuilder methodBody = new StringBuilder()
            methodBody.append("protected void onCreate() {")
            methodBody.append(getAppInjectContent())
            methodBody.append("super.onCreate();")
            methodBody.append("}")
            ctClassApplication.addMethod(CtMethod.make(methodBody.toString(), ctClassApplication))
        } catch (Exception e) {
            println("could not create onCreate() in Application;   " + e.toString())
        }
        ctClassApplication.writeFile(filePath)
//        ctClassApplication.detach()

        println("injectApplicationCode success ")
    }

    private static String getAppInjectContent() {
        return """ com.example.performance_android.AutoSpeed.getInstance().init(this);
               """
    }

    private static String getActOnCreateContent() {
        return """ com.example.performance_android.AutoSpeed.getInstance().onPageCreate(this);
               """
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
                System.out.println(">>>")
                System.out.println(">>>")
                System.out.println(">>>")
                System.out.println(">>>")


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
