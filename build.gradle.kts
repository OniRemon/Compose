plugins {
    id("com.android.application") version "8.2.2" apply false
    id("com.android.library") version "8.2.2" apply false
    id("org.jetbrains.kotlin.android") version "1.9.0" apply false
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.0" apply false
    id("com.google.devtools.ksp") version "1.9.0-1.0.13" apply false
    id("org.jetbrains.kotlin.kapt") version "1.9.0" apply false
    id("com.google.dagger.hilt.android") version "2.47" apply false
}

tasks.register<Delete>("clean") {
    delete(rootProject.buildDir)
}