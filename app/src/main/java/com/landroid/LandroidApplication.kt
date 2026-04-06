// app/src/main/java/com/landroid/LandroidApplication.kt
package com.landroid

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import org.opencv.android.OpenCVLoader

@HiltAndroidApp
class LandroidApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize OpenCV
        OpenCVLoader.initDebug()
    }
}
