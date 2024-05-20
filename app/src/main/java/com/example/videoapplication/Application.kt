package com.example.videoapplication

import android.app.Application
import com.example.videoapplication.video_activity_feature.presentation.viewmodels.ResourcesProvider

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        ResourcesProvider.set(applicationContext)
    }

}