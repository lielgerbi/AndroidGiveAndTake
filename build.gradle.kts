import org.gradle.api.tasks.Delete

buildscript {
    dependencies {
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.40.1")
        classpath("com.google.gms:google-services:4.3.13")

        val nav_version = "2.5.0"
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:$nav_version")
    }
}

plugins {
    id("com.android.application") version "8.1.3" apply false
    id("org.jetbrains.kotlin.android") version "1.8.0" apply false
}

tasks.register<Delete>("clean") {
    delete(rootProject.buildDir)
}
