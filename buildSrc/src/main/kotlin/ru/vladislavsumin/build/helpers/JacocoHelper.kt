package ru.vladislavsumin.build.helpers

import org.gradle.api.Project
import org.gradle.kotlin.dsl.create
import org.gradle.testing.jacoco.tasks.JacocoReport

object JacocoHelper {
    private val GENERATED_CODE = arrayListOf(
        "**/R.class",
        "**/R$*.class",

        "androidx/**",
        "com/google/**",

        "**/*_*.class",
        "**/*$$*",

        "**/BuildConfig.class",
        "**/Dagger*.class"
    )

    private val EXCLUDED_CODE = arrayListOf(
        "**/*Module.class"
    )

    fun setupJacocoTasks(project: Project) {
        project.tasks.create("jacocoTestReport", JacocoReport::class) {
            group = "Reporting"
            dependsOn("testDebugUnitTest")

            val javaClassDirs =
                project.fileTree("${project.buildDir}/intermediates/javac/debug/classes/").apply {
                    exclude(GENERATED_CODE)
                    exclude(EXCLUDED_CODE)
                }
            val kotlinClassDirs =
                project.fileTree("${project.buildDir}/tmp/kotlin-classes/debug/").apply {
                    exclude(GENERATED_CODE)
                    exclude(EXCLUDED_CODE)
                }

            val srcDir = "${project.projectDir}/src/main/java"

            classDirectories.setFrom(javaClassDirs, kotlinClassDirs)
            sourceDirectories.setFrom(srcDir)
            executionData.setFrom(project.files("${project.buildDir}/jacoco/testDebugUnitTest.exec"))
            reports {
                xml.isEnabled = true
            }
        }
    }
}