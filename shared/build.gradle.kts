import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    //for serializations
    id("org.jetbrains.kotlin.plugin.serialization")
}

kotlin {
    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "Shared"
            isStatic = true
        }
    }
    
    androidLibrary {
       namespace = "com.excaution.riwayaapp.shared"
       compileSdk = libs.versions.android.compileSdk.get().toInt()
       minSdk = libs.versions.android.minSdk.get().toInt()
    
       compilerOptions {
           jvmTarget = JvmTarget.JVM_11
       }
       androidResources {
           enable = true
       }
       withHostTest {
           isIncludeAndroidResources = true
       }
    }
    
    sourceSets {
        androidMain.dependencies {
            implementation(libs.compose.uiToolingPreview)
            //platform specific
            implementation("io.ktor:ktor-client-okhttp:3.5.1")
        }

        iosMain.dependencies {
            //platform specific
            implementation("io.ktor:ktor-client-darwin:3.5.1")
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
            //added for navigation()
            implementation("org.jetbrains.androidx.navigation:navigation-compose:2.9.2")
            implementation("org.jetbrains.compose.material:material-icons-extended:1.7.3")
            implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:1.11.0")

            // Official Jetpack ViewModel Multiplatform (v2.8.0+)
            implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")
            implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.7")

            // 3. Multiplatform DataStore Preferences
            implementation("androidx.datastore:datastore-preferences:1.2.1")

            // 4. Coil 3.x Image Loading Extensions
            implementation("io.coil-kt.coil3:coil-compose:3.5.0")
            implementation("io.coil-kt.coil3:coil-network-ktor3:3.5.0")

            //date time
            implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.8.0")

            // Koin Core & Compose Extension
            implementation("io.insert-koin:koin-core:4.2.2")
            implementation("io.insert-koin:koin-compose-viewmodel:4.2.2")
            implementation("io.insert-koin:koin-compose:4.2.2") // Best practice for CMP UI integration

            // Ktor Core & Plugins ---
            implementation("io.ktor:ktor-client-core:3.5.1")
            implementation("io.ktor:ktor-client-content-negotiation:3.5.1")
            implementation("io.ktor:ktor-serialization-kotlinx-json:3.5.1")
            implementation("io.ktor:ktor-client-logging:3.5.1")
            implementation("io.ktor:ktor-client-auth:3.5.1")

            // For local encrypted/plain JWT storage
            implementation("com.russhwolf:multiplatform-settings:1.3.0")

        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

dependencies {
    androidRuntimeClasspath(libs.compose.uiTooling)
}