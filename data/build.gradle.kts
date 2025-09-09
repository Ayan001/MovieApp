plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.android.junit)
    alias(libs.plugins.ksp)
    //alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.appdemo.data"
    compileSdk = 35

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }
    /*testOptions {
        unitTests.all {
            useJUnitPlatform() // 👈 enables JUnit 5 runner
        }
    }*/
    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    packaging {
        resources {
            excludes += setOf(
                "META-INF/AL2.0",
                "META-INF/LGPL2.1",
                "META-INF/DEPENDENCIES",
                "META-INF/*.kotlin_module",
                "META-INF/INDEX.LIST",
                "META-INF/io.netty.versions.properties"
            )
        }
    }
    sourceSets {
        getByName("main").java.srcDirs("src/main/java")
        getByName("test").java.srcDirs("src/test/java")
        getByName("androidTest").java.srcDirs("src/androidTest/java")
    }
}

dependencies {

    implementation(project(":core"))
    implementation(libs.retrofit)
    implementation(libs.logging.interceptor)
    //api(libs.kotlinx.serialization.json)
    //implementation(libs.retrofit2.kotlinx.serialization.converter)
    implementation(project(":domain"))
    implementation(libs.firebase.appdistribution.gradle)
    //testImplementation(libs.bundles.unittest)
    implementation(libs.kotlin.reflect)
    implementation(libs.hilt.android)
    //androidTestImplementation(libs.junit.junit)
    //androidTestImplementation(libs.jupiter.junit.jupiter)
    ksp(libs.hilt.android.compiler)
    implementation(libs.paging.runtime)
    implementation(libs.paging.compose)
    testImplementation(libs.paging.common)
    implementation(libs.converter.gson)
    implementation(libs.converter.scalars)
    testImplementation(libs.bundles.unittest)
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.kotest.assertions.core)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.androidx.junit)
    testImplementation(libs.junit.jupiter.api) // or "org.junit.jupiter:junit-jupiter-api:5.9.3"
    testRuntimeOnly(libs.junit.jupiter.engine) // or "org.junit.jupiter:junit-jupiter-engine:5.9.3"


}