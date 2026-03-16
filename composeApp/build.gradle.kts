import org.gradle.kotlin.dsl.implementation
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeHotReload)
}

kotlin {
    jvmToolchain(17)

    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }
    
    jvm {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }
    
    sourceSets {
        androidMain.dependencies {
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.activity.compose)
        }
        commonMain.dependencies {
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            
            implementation(libs.composeIcons.core)
            implementation(libs.composeIcons.extended)

            // Voyager
            implementation(libs.voyager.navigator)
            implementation(libs.voyager.screenModel)
            implementation(libs.voyager.tabNavigator)
            implementation(libs.voyager.transitions)

            implementation(projects.shared)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutinesSwing)
            implementation(libs.logback)
        }
    }
}

android {
    namespace = "dev.wdona.burntout"
    compileSdkVersion(libs.versions.android.compileSdk.get().toInt())

    defaultConfig {
        applicationId = "dev.wdona.burntout"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.1.13"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "/META-INF/DEPENDENCIES"
            excludes += "/META-INF/LICENSE*"
            excludes += "/META-INF/NOTICE*"
            excludes += "/META-INF/*.kotlin_module"
            excludes += "**/kotlin/**"
            excludes += "**/*.txt"
            excludes += "/META-INF/io.netty.versions.properties"
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
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    debugImplementation(libs.compose.uiTooling)
}

compose.desktop {
    application {
        mainClass = "dev.wdona.burntout.MainKt"

        nativeDistributions {
            // WIN - DEB - ARCH
            targetFormats(TargetFormat.Msi, TargetFormat.Deb, TargetFormat.Rpm)
            packageName = "BurntOutApp"
            packageVersion = "1.1.14"
            
            modules("java.sql")

            buildTypes.release.proguard {
                isEnabled.set(false)
            }

            linux {
                shortcut = true
                menuGroup = "Office"
                appCategory = "Office"
            }

            windows {
                shortcut = true
                menu = true
                menuGroup = "BurntOut"
            }
        }
    }
}
