import org.jetbrains.kotlin.config.KotlinCompilerVersion
import ru.vladislavsumin.build.Dependencies
import com.android.build.gradle.internal.api.BaseVariantOutputImpl
import ru.vladislavsumin.build.helpers.JacocoHelper

plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("android.extensions")
    kotlin("kapt")
}

androidExtensions {
    isExperimental = true
}

android {
    val pVersionCode: String by project
    val pVersionNamePrefix: String by project
    val pBuildAgent: String by project
    val pUseUploadSignature: String by project

    compileSdkVersion(29)

    defaultConfig {
        applicationId = "ru.vladislavsumin.myhomeiot"
        minSdkVersion(21)
        targetSdkVersion(29)
        versionCode = pVersionCode.toInt()
        versionName = "$pVersionNamePrefix.$pVersionCode-$pBuildAgent"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val pEnableFirebase: String by project
        buildConfigField("boolean", "ENABLE_FIREBASE", pEnableFirebase)
    }

    signingConfigs {
        create("shared") {
            storeFile = file("../keystore/shared.keystore")
            storePassword = "Qwerty!@"
            keyAlias = "shared"
            keyPassword = "Qwerty!@"
        }

        if (pUseUploadSignature.toBoolean()) {
            val pUploadSignaturePath: String by project
            val pUploadSignaturePassword: String by project
            val pUploadSignatureKeyName: String by project
            val pUploadSignatureKeyPassword: String by project

            create("upload") {
                storeFile = file(pUploadSignaturePath)
                storePassword = pUploadSignaturePassword
                keyAlias = pUploadSignatureKeyName
                keyPassword = pUploadSignatureKeyPassword
            }
        }
    }

    buildTypes {
        getByName("release") {
            versionNameSuffix = "-release"
            isMinifyEnabled = true
            isShrinkResources = true
            isDebuggable = false
            if (pUseUploadSignature.toBoolean()) signingConfig = signingConfigs.getByName("upload")
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }

        getByName("debug") {
            isTestCoverageEnabled = true
            versionNameSuffix = "-debug"
            signingConfig = signingConfigs.getByName("shared")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions.apply {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
    }
}

// Change apk name
afterEvaluate {
    android.applicationVariants.forEach { applicationVariant ->
        applicationVariant.outputs.forEach {
            it as BaseVariantOutputImpl
            it.outputFileName =
                "MyHomeIot-${android.defaultConfig.versionName}-${applicationVariant.name}.apk"
        }
    }
}

dependencies {
    Dependencies.apply {
        //        implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
        implementation(kotlin("stdlib-jdk8", KotlinCompilerVersion.VERSION))

        // design
        implementation(appCompat)
        implementation(material)
        implementation(constraintLayout)
//        implementation(recyclerView)
//        implementation(swipeRefreshLayout)
//        implementation(cardView)
//        implementation(swipeLayout)
//        implementation(loadingButton)

        implementation("androidx.core:core-ktx:1.0.2")
        implementation("androidx.navigation:navigation-fragment:2.0.0")
        implementation("androidx.navigation:navigation-fragment-ktx:2.0.0")
        implementation("androidx.navigation:navigation-ui:2.0.0")
        implementation("androidx.navigation:navigation-ui-ktx:2.0.0")

        implementation(dagger)
        kapt(daggerCompiler)
        implementation("javax.annotation:javax.annotation-api:1.3.2")

        implementation(moxy)
        implementation(moxyAppCompat)
        kapt(moxyCompiler)

        implementation(rxJava)
        implementation(rxKotlin)
        implementation(rxAndroid)

//        implementation(retrofit)
//        implementation(retrofitAdapterRxJava)
//        implementation(retrofitConverterGson)

        implementation("com.google.firebase:firebase-analytics:17.2.0")
        implementation("com.crashlytics.sdk.android:crashlytics:2.10.1")

        //leakcanary
//        debugImplementation("com.squareup.leakcanary:leakcanary-android:2.0-alpha-3")

        testImplementation("junit:junit:4.12")
        testImplementation("org.mockito:mockito-core:3.2.0")

        androidTestImplementation("androidx.test:runner:1.2.0")
        androidTestImplementation("androidx.test.ext:junit:1.1.1")
        androidTestImplementation("androidx.test:rules:1.2.0")
        androidTestImplementation("androidx.test.espresso:espresso-core:3.2.0")


//    implementation("org.jetbrains.kotlinx:kotlinx-metadata-jvm:+")

        // lifecycle
//    implementation("androidx.lifecycle:lifecycle-extensions:2.0.0")
//    implementation("androidx.lifecycle:lifecycle-runtime:2.0.0")
//    implementation("androidx.lifecycle:lifecycle-reactivestreams:2.0.0")
//    kapt("androidx.lifecycle:lifecycle-compiler:2.0.0")

        // paging
//    implementation("androidx.paging:paging-runtime-ktx:2.1.0")
//    testImplementation("androidx.paging:paging-common-ktx:2.1.0")
//    implementation("androidx.paging:paging-rxjava2-ktx:2.1.0")

        // room
        implementation(room)
        kapt(roomCompiler)
        implementation(roomRxJava)
//    testImplementation("androidx.room:room-testing:2.1.0-alpha04")
    }
}

// Add Firebase analytics and Crashlytics plugins
apply {
    plugin("com.google.gms.google-services")
    plugin("io.fabric")
}

JacocoHelper.setupJacocoTasks(project)