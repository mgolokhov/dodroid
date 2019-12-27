import org.gradle.api.JavaVersion


object Config {
    val minSdkVer = 21
    val targetSdkVer = 28
    val compileSdkVer = 28
    val buildtoolsVer = "29.0.2"
    val javaVersion = JavaVersion.VERSION_1_8
}

object Versions {
    val retrofitVer = "2.6.2"
    val appCompatVer = "1.1.0"
    val junitVer = "4.12"
    val truthVer = "1.0"
    val preferenceVer = "1.1.0"
    val testCoreVer = "1.2.0"
    val mockitoCoreVer = "2.23.0"
    val cardViewVer = "1.0.0"
    val recyclerViewVer = "1.1.0"
    val materialVer = "1.0.0"
    val androidXLegacySupportVer = "1.0.0"
    val okhttp3LoggingInterceptorVer = "4.2.2"
    val playServicesAnalyticsVer = "17.0.0"
    val timberVer = "4.7.1"
    val gsonVer = "2.8.6"
    val crashlyticsVer = "2.10.1"
    val coroutinesVer = "1.3.2"
    val roomVer = "2.2.3"
    val daggerVer = "2.23.2"
    val archLifecycleExtVer = "1.1.1"
    val webDebugDbVer = "1.0.6"
    val constraintLayoutVer = "1.1.3"
    val navigationVer = "2.1.0"
    val lifecycleCommonJava8Ver = "2.1.0"
    val phoenix = "2.0.0"
    val stetho = "1.5.1"

    val gradleandroid = "3.5.2"
    val kotlin = "1.3.61"
}

object Deps {
    val tools_gradleandroid = "com.android.tools.build:gradle:${Versions.gradleandroid}"
    val tools_kotlin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin}"
    // Libraries for Testing
    val junitVer = "junit:junit:${Versions.junitVer}"
    // performing assertions
    val truthVer = "com.google.truth:truth:${Versions.truthVer}"
    val testCoreVer = "androidx.test:core:${Versions.testCoreVer}"
    val mockitoCoreVer = "org.mockito:mockito-core:${Versions.mockitoCoreVer}"
    // UI
    val appCompatVer = "androidx.appcompat:appcompat:${Versions.appCompatVer}"
    val preferenceVer = "androidx.preference:preference:${Versions.preferenceVer}"
    val cardViewVer = "androidx.cardview:cardview:${Versions.cardViewVer}"
    val recyclerViewVer = "androidx.recyclerview:recyclerview:${Versions.recyclerViewVer}"
    val materialVer = "com.google.android.material:material:${Versions.materialVer}"
    val androidXLegacySupportVer = "androidx.legacy:legacy-support-v4:${Versions.androidXLegacySupportVer}"
    // Network: Retrofit & OkHttp
    val retrofitVer = "com.squareup.retrofit2:retrofit:${Versions.retrofitVer}"
    val retrofitConvertorGsonVer = "com.squareup.retrofit2:converter-gson:${Versions.retrofitVer}"
    val okhttp3LoggingInterceptorVer = "com.squareup.okhttp3:logging-interceptor:${Versions.okhttp3LoggingInterceptorVer}"
    // Analytics
    val playServicesAnalyticsVer = "com.google.android.gms:play-services-analytics:${Versions.playServicesAnalyticsVer}"
    val timberVer = "com.jakewharton.timber:timber:${Versions.timberVer}"
    val gsonVer = "com.google.code.gson:gson:${Versions.gsonVer}"
    val crashlyticsVer = "com.crashlytics.sdk.android:crashlytics:${Versions.crashlyticsVer}@aar"

    val coroutinesCoreVer = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.coroutinesVer}"
    val coroutinesAndroidVer = "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.coroutinesVer}"

    val roomRuntimeVer = "androidx.room:room-runtime:${Versions.roomVer}"
    val roomCompilerVer = "androidx.room:room-compiler:${Versions.roomVer}"
    // Kotlin Extensions and Coroutines support for Room
    val roomKtxVer = "androidx.room:room-ktx:${Versions.roomVer}"
    // RxJava support for Room
    val roomRxjava2Ver = "androidx.room:room-rxjava2:${Versions.roomVer}"
    // Test helpers
    val roomTestingVer = "androidx.room:room-testing:${Versions.roomVer}"

    val daggerVer  = "com.google.dagger:dagger:${Versions.daggerVer}"
    val daggerCompilerVer = "com.google.dagger:dagger-compiler:${Versions.daggerVer}"

    val archLifecycleExtVer = "android.arch.lifecycle:extensions:${Versions.archLifecycleExtVer}"

    val webDebugDbVer = "com.amitshekhar.android:debug-db:${Versions.webDebugDbVer}"

    val constraintLayoutVer = "androidx.constraintlayout:constraintlayout:${Versions.constraintLayoutVer}"

    val navigationFragmentVer = "androidx.navigation:navigation-fragment:${Versions.navigationVer}"
    val navigationUiVer = "androidx.navigation:navigation-ui:${Versions.navigationVer}"
    val navigationLifecycleExtVer = "androidx.lifecycle:lifecycle-extensions:${Versions.navigationVer}"
    val navigationFragmentKtxVer = "androidx.navigation:navigation-fragment-ktx:${Versions.navigationVer}"
    val navigationUiKtxVer = "androidx.navigation:navigation-ui-ktx:${Versions.navigationVer}"

    val lifecycleCommonJava8Ver = "androidx.lifecycle:lifecycle-common-java8:${Versions.lifecycleCommonJava8Ver}"
    // used in debug menu to restart application process
    val phoenix = "com.jakewharton:process-phoenix:${Versions.phoenix}"
    // a debug bridge to inspect Database, Network, Sharedpreferences
    val stetho = "com.facebook.stetho:stetho:${Versions.stetho}"
    val stethoOkhttp3Interceptor = "com.facebook.stetho:stetho-okhttp3:${Versions.stetho}"
}