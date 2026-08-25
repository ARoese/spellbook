import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

kotlin {
    dependencies {
        implementation(projects.shared)
        implementation(compose.desktop.currentOs)
        implementation(libs.skiko.awt.runtime.windows.x64)
        implementation(libs.kotlinx.coroutines.swing)

        implementation("ch.qos.logback:logback-classic:1.5.18")
    }
}

compose.desktop {
    application {
        mainClass = "org.fufu.spellbook.MainKt"

        buildTypes.release.proguard {
            this.isEnabled = false
        }

        nativeDistributions {
            modules("java.naming", "jdk.unsupported")
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = libs.versions.spellbookPackageName.get()
            packageVersion = libs.versions.spellbook.get()
            windows {
                upgradeUuid = "EE6E75A4-5486-4127-AA3E-C61812A81919"
                perUserInstall = true
                iconFile = project(":shared").file("src/commonMain/composeResources/drawable/app_icon.ico")
                shortcut = true
            }
        }
    }
}