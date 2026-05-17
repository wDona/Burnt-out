import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.gradle.api.provider.ValueSource
import org.gradle.api.provider.ValueSourceParameters
import org.gradle.process.ExecOperations
import javax.inject.Inject
import java.io.ByteArrayOutputStream

abstract class GitVersionValueSource : ValueSource<String, ValueSourceParameters.None> {
    @get:Inject
    abstract val execOperations: ExecOperations

    override fun obtain(): String {
        return try {
            val output = ByteArrayOutputStream()
            execOperations.exec {
                commandLine("git", "rev-list", "--count", "HEAD")
                standardOutput = output
            }
            val count = output.toString().trim().toLong()
            val majorPart = count / 100
            val minorPart = String.format("%02d", count % 100)
            "1.$majorPart.$minorPart"
        } catch (_: Exception) {
            "1.0.0"
        }
    }
}

val appVersion = if (project.hasProperty("appVersion")) {
    project.property("appVersion") as String
} else {
    providers.of(GitVersionValueSource::class) {}.get()
}

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinSerialization)
    // composeHotReload removido por incompatibilidad con Kotlin 2.1.0
}

kotlin {
    jvmToolchain(17)
    
    compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }

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
            implementation(libs.androidx.core.ktx)
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
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(libs.sqlDelight.driver.sqlite)
        }
    }
}

android {
    namespace = "dev.wdona.burntout"
    compileSdk = 36

    defaultConfig {
        applicationId = "dev.wdona.burntout"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = appVersion
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
        buildConfig = true
    }
}

dependencies {
    debugImplementation(libs.compose.uiTooling)
}

compose.desktop {
    application {
        mainClass = "dev.wdona.burntout.MainKt"
        jvmArgs += "-Dapp.version=$appVersion"

        nativeDistributions {
            // WIN - DEB - ARCH
            targetFormats(TargetFormat.Msi, TargetFormat.Deb, TargetFormat.Rpm)
            packageName = "BurntOut"
            packageVersion = appVersion
            
            modules("java.sql")

            buildTypes.release.proguard {
                isEnabled.set(false)
            }

            linux {
                shortcut = true
                menuGroup = "Office"
                appCategory = "Office"
                iconFile.set(project.file("src/jvmMain/resources/logoBurntOutIcon.png"))
            }

            windows {
                shortcut = true
                menu = true
                menuGroup = "BurntOut"
                iconFile.set(project.file("src/jvmMain/resources/logoBurntOutIcon.ico"))
            }
        }
    }
}
