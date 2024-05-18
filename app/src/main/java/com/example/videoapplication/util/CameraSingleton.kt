package com.example.videoapplication.util

import android.content.Context
import androidx.camera.view.LifecycleCameraController

object CameraSingleton {
    private var instance: LifecycleCameraController? = null

    fun getInstance(context: Context): LifecycleCameraController {
        if(instance == null) instance = LifecycleCameraController(context)
        return instance as LifecycleCameraController
    }

    fun destroyInstance() {
        instance = null
    }
}