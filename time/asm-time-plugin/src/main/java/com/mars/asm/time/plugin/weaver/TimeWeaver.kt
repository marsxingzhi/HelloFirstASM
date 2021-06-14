package com.mars.asm.time.plugin.weaver

import com.mars.asm.time.plugin.visitor.TimeClassVisitor
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Opcodes
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

/**
 * Created by geyan on 2021/5/9
 */
class TimeWeaver : IWeaver {

    override fun weave(inputPath: String, outputPath: String) {
        println(">>>>>> start TimeComputeWeaver weave <<<<<<")
        println("inputPath: $inputPath, outputPath: $outputPath")
        try {
            val fileInputStream = FileInputStream(inputPath)
            val classReader = ClassReader(fileInputStream)
            val classWriter = ClassWriter(classReader, ClassWriter.COMPUTE_FRAMES)
            val timeClassVisitor = TimeClassVisitor(classWriter)
            classReader.accept(timeClassVisitor, ClassReader.EXPAND_FRAMES)

            val bytes = classWriter.toByteArray()
            val fileOutputStream = FileOutputStream(File(outputPath))
            fileOutputStream.write(bytes)
            fileOutputStream.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        println(">>>>>> end TimeComputeWeaver weave <<<<<<")
    }

}