package com.mars.asm.time.plugin.weaver

/**
 * Created by geyan on 2021/5/9
 */
interface IWeaver {

    /**
     * 处理class文件，
     *
     * 输入class文件的路径：inputPath，经过处理后，输出到outputPath路径下的文件
     */
    fun weave(inputPath: String, outputPath: String)
}