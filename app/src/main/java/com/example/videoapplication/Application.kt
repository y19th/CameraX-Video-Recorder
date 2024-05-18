package com.example.videoapplication

import android.app.Application
import com.example.videoapplication.presentation.viewmodels.ResourcesProvider

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        ResourcesProvider.set(applicationContext)
    }

}