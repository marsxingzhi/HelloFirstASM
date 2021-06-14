package com.mars.asm.plugin

import com.android.build.api.transform.Format
import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.Transform
import com.android.build.api.transform.TransformInvocation
import com.android.build.gradle.internal.pipeline.TransformManager
import com.android.utils.FileUtils
import com.mars.asm.plugin.visitor.SimpleClassVisitor
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

/**
 * Created by geyan on 2021/6/14
 */
class SimpleAsmTransform : Transform() {

    /**
     * 获取Transform的名字
     */
    override fun getName(): String {
        return SimpleAsmTransform::class.java.name
    }

    /**
     * 消费的文件类型，一般是CONTENT_CLASS
     */
    override fun getInputTypes(): MutableSet<QualifiedContent.ContentType> {
        return TransformManager.CONTENT_CLASS
    }

    /**
     * 是否支持增量编译，注意增量编译需要额外的支持
     */
    override fun isIncremental(): Boolean {
        return false
    }

    /**
     * 作用范围：
     * 1. PROJECT: 只处理当前项目
     * 2. SUB_PROJECTS: 只处理子项目
     * 3. PROJECT_LOCAL_DEPS: 只处理当前项目的本地依赖，例如 jar和aar等
     * 4. EXTERNAL_LIBRARIES: 只处理外部的依赖库
     * 5. PROVIDED_ONLY: 只处理本地或远程以provided形式引入的依赖库
     * 6. TESTED_CODE: 只处理测试代码
     */
    override fun getScopes(): MutableSet<in QualifiedContent.Scope> {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    override fun transform(transformInvocation: TransformInvocation?) {
        super.transform(transformInvocation)

        transformInvocation ?: return

        if (!transformInvocation.isIncremental) {
            transformInvocation.outputProvider.deleteAll()
        }

        // 消费型输入，可以从中获取jar包和class文件夹路径，需要输出给下一个任务
        val inputs = transformInvocation.inputs
        // outputProvider管理消费型输出，如果消费型输入为空，你会发现outputProvider为null
        val outputProvider = transformInvocation.outputProvider

        inputs?.forEach { input ->
            input.jarInputs?.forEach {
                val dest = outputProvider?.getContentLocation(
                    it.file.absolutePath,
                    it.contentTypes,
                    it.scopes,
                    Format.JAR
                )
                // 将修改后的字节码copy到dest，就可以实现编译期间干预字节码的目的了
                FileUtils.copyFile(it.file, dest)
            }
            input.directoryInputs?.forEach {
                val dest = outputProvider?.getContentLocation(
                    it.name,
                    it.contentTypes,
                    it.scopes,
                    Format.DIRECTORY
                )
                transformDir(it.file, dest)
            }
        }
    }

    private fun transformDir(input: File?, dest: File?) {
        if (input == null || dest == null) {
            return
        }
        if (dest.exists()) {
            FileUtils.delete(dest)
        }
        FileUtils.mkdirs(dest)
        val srcDirPath = input.absolutePath
        val destDirPath = dest.absolutePath

        input.listFiles()?.forEach {
            val destFilePath = it.absolutePath.replace(srcDirPath, destDirPath)
            val destFile = File(destFilePath)
            if (it.isDirectory) {
                transformDir(it, destFile)
            } else if(it.isFile){
                weave(it.absolutePath, destFile.absolutePath)
            }
        }
    }

    /**
     * 一定需要try-catch，由于是遍历文件并调用weave方法，肯定存在空的情况
     */
    private fun weave(inputPath: String, outputPath: String) {
        try {
            val inputStream = FileInputStream(inputPath)
            // 该类用来解析编译过的class字节码文件
            val cr = ClassReader(inputStream)
            // 该类用来重新构建编译后的类，比如说修改类名、属性以及方法，甚至可以生成新的类的字节码文件
            val cw = ClassWriter(ClassWriter.COMPUTE_FRAMES)
            val adapter = SimpleClassVisitor(cw)
            cr.accept(adapter, ClassReader.EXPAND_FRAMES)
            val outputStream = FileOutputStream(outputPath)
            outputStream.write(cw.toByteArray())
            outputStream.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}