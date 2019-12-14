package ru.vladislavsumin.build

object Dependencies {
    const val appCompat = "androidx.appcompat:appcompat:${Versions.appCompat}"
    const val constraintLayout = "androidx.constraintlayout:constraintlayout:1.1.3"
    const val recyclerView = "androidx.recyclerview:recyclerview:1.0.0"
    const val swipeRefreshLayout = "androidx.swiperefreshlayout:swiperefreshlayout:1.0.0"
    const val material = "com.google.android.material:material:1.0.0"
    const val cardView = "androidx.cardview:cardview:1.0.0"
    const val swipeLayout = "com.daimajia.swipelayout:library:1.2.0@aar"
    const val loadingButton = "br.com.simplepass:loading-button-android:2.1.5"

    const val dagger = "com.google.dagger:dagger:${Versions.dagger}"
    const val daggerCompiler = "com.google.dagger:dagger-compiler:${Versions.dagger}"

    const val moxy = "com.github.moxy-community:moxy:${Versions.moxy}"
    const val moxyCompiler = "com.github.moxy-community:moxy-compiler:${Versions.moxy}"
    const val moxyAppCompat = "com.github.moxy-community:moxy-app-compat:${Versions.moxy}"

    const val rxJava = "io.reactivex.rxjava2:rxjava:${Versions.rxJava}"
    const val rxKotlin = "io.reactivex.rxjava2:rxkotlin:${Versions.rxKotlin}"
    const val rxAndroid = "io.reactivex.rxjava2:rxandroid:${Versions.rxAndroid}"

    const val room = "androidx.room:room-runtime:${Versions.room}"
    const val roomCompiler = "androidx.room:room-compiler:${Versions.room}"
    const val roomRxJava = "androidx.room:room-rxjava2:${Versions.room}"

    const val retrofit = "com.squareup.retrofit2:retrofit:${Versions.retrofit}"
    const val retrofitAdapterRxJava = "com.squareup.retrofit2:adapter-rxjava2:${Versions.retrofit}"
    const val retrofitConverterGson = "com.squareup.retrofit2:converter-gson:${Versions.retrofit}"
}