package ru.vladislavsumin.build.helpers

import org.gradle.api.Project
import org.gradle.kotlin.dsl.create
import org.gradle.testing.jacoco.tasks.JacocoReport

object JacocoHelper {
    private val GENERATED_CODE = arrayListOf(
        "**/R.class",
        "androidx/**",
        "com/google/**"
    )

    fun setupJacocoTasks(project: Project) {
        project.tasks.create("jacocoTestReport", JacocoReport::class) {
            group = "Reporting"
            dependsOn("testDebugUnitTest")

            val classDirs =
                project.fileTree("${project.buildDir}/intermediates/javac/debug/classes/")
                    .exclude(GENERATED_CODE)

            val srcDir = "${project.projectDir}/src/main/java"

            classDirectories.setFrom(classDirs)
            sourceDirectories.setFrom(srcDir)
            executionData.setFrom(project.files("${project.buildDir}/jacoco/testDebugUnitTest.exec"))
            reports {
                xml.isEnabled = true
            }
        }
    }
}