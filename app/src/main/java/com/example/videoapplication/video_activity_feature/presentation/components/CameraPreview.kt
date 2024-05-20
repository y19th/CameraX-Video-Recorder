package com.example.videoapplication.video_activity_feature.presentation.components

import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun CameraPreviewScreen(
    modifier: Modifier = Modifier,
    lifecycleCameraController: LifecycleCameraController,
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    AndroidView(
        factory = {
            PreviewView(it).apply {
                this.controller = lifecycleCameraController
                lifecycleCameraController.bindToLifecycle(lifecycleOwner)
            }
        },
        modifier = modifier
    )
}