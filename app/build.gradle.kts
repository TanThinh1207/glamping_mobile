import com.android.build.api.variant.BuildConfigField
import org.jetbrains.kotlin.gradle.plugin.extraProperties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android") version "2.1.0"
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
    id("kotlin-parcelize")
    id("com.google.gms.google-services")
    id("androidx.navigation.safeargs.kotlin")
}

android {
    namespace = "com.avocado.glamping"
    compileSdk = 35
    buildFeatures{
        buildConfig = true
    }

    defaultConfig {

        applicationId = "com.avocado.glamping"
        minSdk = 30
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"


        buildConfigField("String", "API_URL", project.properties["API_URL"].toString())
        buildConfigField("String", "DOMAIN", project.properties["DOMAIN"].toString())
        buildConfigField("String", "COUNTRY", project.properties["COUNTRY"].toString())
        buildConfigField("String", "LANGUAGE", project.properties["LANGUAGE"].toString())
        buildConfigField("String", "SERVER_CLIENT_ID", project.properties["SERVER_CLIENT_ID"].toString())
        buildConfigField("String", "WEBSOCKET_URL", project.properties["WEBSOCKET_URL"].toString())
        manifestPlaceholders["MAPS_API_KEY"] = project.properties["MAPS_API_KEY"].toString()
    }

    task("replaceIpInNetworkConfig"){
        doLast{

        }
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }

}



dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.messaging)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.google.dagger:hilt-android:2.51.1")
    kapt("com.google.dagger:hilt-compiler:2.51.1") // Use the latest version
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.5.1") // Or the latest version
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.5.1")
    implementation("androidx.navigation:navigation-fragment-ktx:2.8.6")
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.3")
    implementation("androidx.navigation:navigation-compose:2.8.6")
    implementation("androidx.navigation:navigation-ui-ktx:2.8.6")
    implementation(platform("com.google.firebase:firebase-bom:33.9.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.android.gms:play-services-auth:20.7.0")
    implementation("com.google.android.gms:play-services-maps:18.2.0")
    implementation("com.google.android.gms:play-services-location:21.0.1")
    implementation("org.locationtech.jts:jts-core:1.19.0")
    implementation("io.coil-kt:coil:2.5.0")
    implementation("androidx.security:security-crypto:1.1.0-alpha06")
    implementation("com.github.denzcoskun:ImageSlideshow:0.1.2")
    implementation("com.google.android.flexbox:flexbox:3.0.0")
    implementation("com.squareup.okhttp3:okhttp:4.10.0")
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
    implementation("com.github.NaikSoftware:StompProtocolAndroid:1.6.6")
    implementation("io.reactivex.rxjava2:rxjava:2.2.21")
    implementation("io.reactivex.rxjava2:rxandroid:2.1.1")
    implementation("androidx.paging:paging-runtime-ktx:3.2.1")
}

kapt {
    correctErrorTypes = true
}
