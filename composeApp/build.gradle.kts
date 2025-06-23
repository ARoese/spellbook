import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.BOOLEAN
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.jetbrains.kotlin.serialization)
    alias(libs.plugins.room)
    alias(libs.plugins.ksp)
    id("com.codingfeline.buildkonfig").version("0.17.1")
}

val fufuPackageName = "org.fufu.spellbook"

buildkonfig {
    packageName = fufuPackageName
    // objectName = "YourAwesomeConfig"
    // exposeObjectWithName = "YourAwesomePublicConfig"

    val isDebugName = "isDebug"
    defaultConfigs {
        buildConfigField(BOOLEAN, isDebugName, false.toString())
    }

    defaultConfigs("dev") {
        buildConfigField(BOOLEAN, isDebugName, true.toString())
    }

}

kotlin {
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes")
        //to prevent variables from getting optimized out
        //freeCompilerArgs.add("-Xdebug")
    }

    jvm("desktop")

    room {
        schemaDirectory("$projectDir/schemas")
    }

    sourceSets {
        val desktopMain by getting

        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)

            implementation(libs.koin.android)
            implementation(libs.koin.androidx.compose)
        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.preview)
            //implementation(compose.material)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtime.compose)

            implementation(libs.jetbrains.compose.navigation)
            implementation(libs.kotlinx.serialization.json)

            implementation(libs.kotlinx.coroutines.core)
            implementation(compose.material3)

            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)
            api(libs.koin.core)
            // room
            implementation(libs.androidx.room.runtime)
            implementation(libs.sqlite.bundled)

            // for filekit desktop
            implementation("net.java.dev.jna:jna:5.17.0")
            implementation("net.java.dev.jna:jna-platform:5.17.0")
            implementation("io.github.vinceglb:filekit-core:0.10.0-beta01")
            implementation("io.github.vinceglb:filekit-dialogs-compose:0.10.0-beta01")

            // DataStore library
            implementation(libs.androidx.datastore)
            // The Preferences DataStore library
            implementation(libs.androidx.datastore.preferences)
            // this needs to be here for the preferences library. It's not implicit...
            implementation(libs.androidx.datastore.preferences.proto)
        }
        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.skiko.awt.runtime.windows.x64)
            implementation(libs.kotlinx.coroutines.swing)
        }

        dependencies {
            ksp(libs.androidx.room.compiler)
        }
    }
}

// NOTE: Make sure to update versionCode when you update this
val versionString = "1.2.1"
val versionCode = 4

android {
    namespace = fufuPackageName
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = fufuPackageName
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = versionCode
        versionName = versionString
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    debugImplementation(compose.uiTooling)
}

compose.desktop {
    application {
        mainClass = "org.fufu.spellbook.MainKt"

        nativeDistributions {
            modules("java.naming")
            modules("jdk.unsupported")
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = fufuPackageName
            packageVersion = versionString
            windows {
                upgradeUuid = "EE6E75A4-5486-4127-AA3E-C61812A81919"
                perUserInstall = true
                iconFile = project.file("src/commonMain/composeResources/drawable/app_icon.ico")
                shortcut = true
            }
        }
    }
}
