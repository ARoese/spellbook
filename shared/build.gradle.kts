import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING
import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.BOOLEAN
import com.codingfeline.buildkonfig.gradle.TargetConfigDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)

    alias(libs.plugins.jetbrains.kotlin.serialization)
    alias(libs.plugins.room)
    alias(libs.plugins.ksp)
    id("com.codingfeline.buildkonfig").version("0.17.1")
}

buildkonfig {
    packageName = libs.versions.spellbookPackageName.get()
    exposeObjectWithName = "SharedBuildKonfig"

    val isDebugName = "isDebug"

    defaultConfigs {
        buildConfigField(BOOLEAN, isDebugName, false.toString())
        buildConfigField(STRING, "buildVersion", libs.versions.spellbook.get())
        buildConfigField(STRING, "spellbookGithubLink", libs.versions.spellbookGithubLink.get())
    }

    defaultConfigs("dev") {
        buildConfigField(BOOLEAN, isDebugName, true.toString())
        buildConfigField(STRING, "buildVersion", libs.versions.spellbook.get())
        buildConfigField(STRING, "spellbookGithubLink", libs.versions.spellbookGithubLink.get())
    }

}

kotlin {
    android {
        namespace = "org.fufu.spellbook.composeLibrary"
        compileSdk = libs.versions.android.compileSdk.get().toInt()

        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }

        androidResources {
            enable = true
        }
    }

    compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes")
        //to prevent variables from getting optimized out
        //freeCompilerArgs.add("-Xdebug")
    }

    jvm("desktop")

    room3 {
        schemaDirectory("$projectDir/schemas")
    }

    sourceSets {
        //val desktopMain by getting

        commonMain.dependencies {
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.uiToolingPreview)
            //implementation(compose.material)
            implementation(libs.compose.ui)
            api(libs.compose.resources)
            implementation(libs.androidx.lifecycle.viewmodel)
            api(libs.androidx.lifecycle.runtime.compose)

            implementation(libs.jetbrains.compose.navigation)
            implementation(libs.kotlinx.serialization.json)

            implementation(libs.kotlinx.coroutines.core)
            api(libs.compose.material3)
            api(libs.compose.material.icons)
            implementation(libs.compose.icons.fontawesome)

            api(libs.koin.compose)
            api(libs.koin.compose.viewmodel)
            api(libs.koin.core)
            // room
            api(libs.androidx.room.runtime)
            api(libs.sqlite.bundled)

            // for filekit desktop
            implementation(libs.jna)
            implementation(libs.net.jna.platform)
            api(libs.filekit)
            api(libs.filekit.dialogs)

            // DataStore library
            api(libs.androidx.datastore)
            // The Preferences DataStore library
            api(libs.androidx.datastore.preferences)
            // this needs to be here for the preferences' library. It's not implicit...
            api(libs.androidx.datastore.preferences.proto)

            implementation(libs.material.icons.core)

            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.slf4j.api)
            implementation(libs.composable.table)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)

        }
    }
}

compose.resources {
    // include the minimum required files from the 5e-database submodule. That module has a whole web server
    // that we don't need; we just want some json files. We don't want to copy them manually into the
    // composeResources dir either, because then we duplicate the files and defeat the point of adding
    // that repo as a subdirectory.

    // This custom task combines the commonMain compose resources dir contents into a build dir,
    // adds the required SRD json files, then sets that combined build dir as the real resources dir
    // That way, the json files included in the build are always up to date, and aren't duplicated in
    // source control
    val srdOutDir = layout.buildDirectory.dir("copySRDResources")
    publicResClass = true
    val copySrd = tasks.register<Sync>("copySRDResources") {
        description = "Copy 5e SRD JSON data to Compose resources and merge Compose resources"

        into(srdOutDir)

        from(layout.projectDirectory.dir("src/commonMain/composeResources"))

        from(layout.projectDirectory.file("5e-database/src/2014/en/5e-SRD-Spells.json")) {
            into("files/srd5e/2014/en/")
        }

        from(layout.projectDirectory.file("5e-database/src/2014/en/5e-SRD-Conditions.json")) {
            into("files/srd5e/2014/en/")
        }
        from(layout.projectDirectory.file("5e-database/src/2014/en/5e-SRD-Subclasses.json")) {
            into("files/srd5e/2014/en/")
        }

        from(layout.projectDirectory.file("5e-database/src/2024/en/5e-SRD-Spells.json")) {
            into("files/srd5e/2024/en/")
        }

        from(layout.projectDirectory.file("5e-database/src/2024/en/5e-SRD-Conditions.json")) {
            into("files/srd5e/2024/en/")
        }
    }

    tasks.named("copyNonXmlValueResourcesForCommonMain") {
        dependsOn(copySrd)
    }
    tasks.named("convertXmlValueResourcesForCommonMain") {
        dependsOn(copySrd)
    }

    customDirectory(
        "commonMain",
        srdOutDir
    )
}

dependencies {
    androidRuntimeClasspath(libs.compose.uiTooling)

    add("kspAndroid", libs.androidx.room.compiler)
    add("kspCommonMainMetadata", libs.androidx.room.compiler)
    add("kspDesktop", libs.androidx.room.compiler)
}
