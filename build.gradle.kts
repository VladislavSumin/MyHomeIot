import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask

buildscript {
    repositories {
        mavenCentral()
        google()
        jcenter()
        maven(url = "https://kotlin.bintray.com/kotlinx/")
    }
    dependencies {
        classpath("com.android.tools.build:gradle:3.5.3")
        classpath(kotlin("gradle-plugin", version = "1.3.61"))
    }
}

allprojects {
    repositories {
        mavenCentral()
        google()
        jcenter()
        maven(url = "https://jitpack.io")
    }
}


plugins {
    idea
    /**
     * Check plugin && library versions
     * Use task "dependencyUpdates" to check updates
     */
    id("com.github.ben-manes.versions") version ("0.27.0")
}

tasks.named<DependencyUpdatesTask>("dependencyUpdates") {
    resolutionStrategy {
        componentSelection {
            all {
                // Disable alpha/beta library versions
                val rejected = listOf(
                    "alpha",
                    "beta",
                    "rc",
                    "cr",
                    "m",
                    "preview",
                    "b",
                    "ea"
                ).any { qualifier ->
                    candidate.version.matches(Regex("(?i).*[.-]$qualifier[.\\d-+]*"))
                }
                if (rejected) reject("Release candidate")
            }
        }
    }
    checkForGradleUpdate = true
}
