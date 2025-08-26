androidApplication {
    namespace = "org.example.app"

    dependencies {
        // Core UI
        implementation("androidx.core:core-ktx:1.13.1")
        implementation("androidx.appcompat:appcompat:1.7.0")
        implementation("com.google.android.material:material:1.12.0")
        implementation("androidx.recyclerview:recyclerview:1.3.2")
        implementation("androidx.constraintlayout:constraintlayout:2.1.4")
        implementation("androidx.fragment:fragment-ktx:1.8.2")

        // Lifecycle
        implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.4")
        implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.8.4")
        implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.4")

        // SQLite (using SQLite directly instead of Room to avoid annotation processing)
        implementation("androidx.sqlite:sqlite-ktx:2.4.0")

        // Coroutines
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")

        // Keep sample dependency linkage to satisfy multiproject structure
        implementation(project(":utilities"))
    }
}
