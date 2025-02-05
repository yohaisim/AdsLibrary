plugins {
    alias(libs.plugins.android.library)
    id("maven-publish") }

android {
    namespace = "com.example.adslibrary"
    compileSdk = 34

    defaultConfig {
        minSdk = 26

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    annotationProcessor(libs.compiler)
    implementation(libs.retrofit)
    api("com.squareup.picasso:picasso:2.71828")
    implementation(libs.converter.gson)
    implementation (libs.glide)
    implementation(libs.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                groupId = "com.github.yohaisim"
                artifactId = "adslibrary"
                version = "1.0.0"

                artifact(tasks.getByName("bundleReleaseAar"))
            }
        }
    }
}




