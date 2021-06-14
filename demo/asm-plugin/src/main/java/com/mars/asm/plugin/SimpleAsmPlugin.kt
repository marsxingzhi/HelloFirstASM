package com.mars.asm.plugin

import com.android.build.gradle.AppExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Created by geyan on 2021/6/14
 */
class SimpleAsmPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        println("invoke SimpleAsmProject apply method")
        project.extensions.getByType(AppExtension::class.java)
            .registerTransform(SimpleAsmTransform())
    }

}