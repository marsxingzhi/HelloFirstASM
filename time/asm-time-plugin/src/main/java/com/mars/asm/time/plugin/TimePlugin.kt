package com.mars.asm.time.plugin

import com.android.build.gradle.AppExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Created by geyan on 2021/6/15
 */
class TimePlugin : Plugin<Project> {

    override fun apply(project: Project) {
        println("invoke TimePlugin apply method")
        project.extensions.getByType(AppExtension::class.java).registerTransform(TimeTransform())
    }
}