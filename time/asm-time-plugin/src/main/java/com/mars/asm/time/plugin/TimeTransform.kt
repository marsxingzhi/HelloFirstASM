package com.mars.asm.time.plugin

import com.android.build.api.transform.Format
import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.Transform
import com.android.build.api.transform.TransformInvocation
import com.android.build.gradle.internal.pipeline.TransformManager
import com.android.ide.common.internal.WaitableExecutor
import com.android.utils.FileUtils
import com.mars.asm.time.plugin.visitor.TimeClassVisitor
import com.mars.asm.time.plugin.weaver.IWeaver
import com.mars.asm.time.plugin.weaver.TimeWeaver
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import kotlin.Exception

/**
 * Created by geyan on 2021/6/15
 */
class TimeTransform : Transform() {

    // 并发处理transform
    private val mWaitableExecutor by lazy {
        WaitableExecutor.useGlobalSharedThreadPool()
    }

    private val mTimeWeaver: IWeaver by lazy {
        TimeWeaver()
    }

    override fun getName(): String {
        return TimeTransform::class.java.name
    }

    override fun getInputTypes(): MutableSet<QualifiedContent.ContentType> {
        return TransformManager.CONTENT_CLASS
    }

    override fun isIncremental(): Boolean {
        return false
    }

    override fun getScopes(): MutableSet<in QualifiedContent.Scope> {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    override fun transform(transformInvocation: TransformInvocation?) {
        super.transform(transformInvocation)

        transformInvocation ?: return

        if (!transformInvocation.isIncremental) {
            transformInvocation.outputProvider.deleteAll()
        }

        val inputs = transformInvocation.inputs
        val outputProvider = transformInvocation.outputProvider

        inputs?.forEach { input ->
            // jar包处理
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
            // dir处理，重点在这里
            input.directoryInputs?.forEach {
                val dest = outputProvider?.getContentLocation(
                    it.file.absolutePath,
                    it.contentTypes,
                    it.scopes,
                    Format.DIRECTORY
                )
                transformDir(it.file, dest)
            }
        }
        // 等待所有的任务被执行
        mWaitableExecutor.waitForTasksWithQuickFail<Any>(true)
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
            } else if (it.isFile) {
//                weave(it.absolutePath, destFile.absolutePath)
                mWaitableExecutor.execute {
                    mTimeWeaver.weave(it.absolutePath, destFile.absolutePath)
                    null
                }
            }
        }
    }

    /**
     * 例如：
     * inputPath: /Users/geyan.marx/learning/TechProjects/ASMProject/app/build/intermediates/javac/debug/classes/com/mars/asmproject/BuildConfig.class
     * outputPath: /Users/geyan.marx/learning/TechProjects/ASMProject/app/build/intermediates/transforms/com.mars.injectime.ComputeTimeTransform/debug/47/com/mars/asmproject/BuildConfig.class
     *
     * 这里inputPath就是输入的class文件的路径，outputPath是输出的class文件的路径。
     * 这里就和之前在Test中一样了，只针对某个class文件处理
     */
    private fun weave(inputPath: String, outputPath: String) {
        try {
            val fileInputStream = FileInputStream(inputPath)

            val classReader = ClassReader(fileInputStream)

            // 选择COMPUTE_FRAMES原因？
            val classWriter = ClassWriter(ClassWriter.COMPUTE_FRAMES)
            val timeClassVisitor = TimeClassVisitor(classWriter)

            classReader.accept(timeClassVisitor, ClassReader.EXPAND_FRAMES)

            val fileOutputStream = FileOutputStream(outputPath)
            fileOutputStream.write(classWriter.toByteArray())
            fileOutputStream.close()
        } catch (e: Exception) {

        }
    }

}