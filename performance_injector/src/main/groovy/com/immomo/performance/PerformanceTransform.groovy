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
    ClassPool classPool
    private String applicationName

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

    private void getRealApplicationName() {
        applicationName = mProject.extensions.performanceInjectorCmd.applicationName
        if (applicationName == null || applicationName.isEmpty()) {
            throw new RuntimeException("you should set applicationName in performanceInjectorCmd")
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

    @Override
    void transform(Context context, Collection<TransformInput> inputs, Collection<TransformInput> referencedInputs, TransformOutputProvider outputProvider, boolean isIncremental) throws IOException, TransformException, InterruptedException {
        System.out.println("----------------进入transform了--------------")
        getRealApplicationName()
        classPool = new ClassPool()
        mProject.android.bootClasspath.each {
            classPool.appendClassPath((String) it.absolutePath)
        }

        def box = ConvertUtil.toCtClasses(inputs, classPool)

        CtClass appCtClass;


        for (CtClass ctClass : box) {
            if (isApplication(ctClass)) {
                appCtClass = ctClass;
                println("app class is " + ctClass.getName())
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

                injectApplicationCode(appCtClass, directoryInput.file.absolutePath)

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
