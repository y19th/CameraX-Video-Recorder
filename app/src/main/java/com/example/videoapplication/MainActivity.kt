package com.example.videoapplication

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.camera.view.CameraController
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.videoapplication.presentation.screens.MainScreen
import com.example.videoapplication.presentation.viewmodels.MainViewModel
import com.example.videoapplication.ui.theme.VideoApplicationTheme
import com.example.videoapplication.util.CameraSingleton

class MainActivity : ComponentActivity() {

    private val viewModel by viewModels<MainViewModel>()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!hasRequiredPermissions()) {
            ActivityCompat.requestPermissions(
                this, CAMERAX_PERMISSIONS, 0
            )
        }

        val intent = Intent(applicationContext, VideoService::class.java)
        applicationContext.startForegroundService(intent)

        registerServiceBroadcastReceiver()


        setContent {
            VideoApplicationTheme {
                val controller = remember {
                    CameraSingleton.getInstance(applicationContext).apply {
                        setEnabledUseCases(CameraController.VIDEO_CAPTURE)
                    }
                }

                Scaffold { paddingValues ->
                    MainScreen(
                        modifier = Modifier.padding(paddingValues),
                        controller = controller,
                        viewModel = viewModel,
                        onRecordVideo = { event ->
                            applicationContext.sendVideoServiceEvent(event)
                        }
                    )
                }

            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onPause() {
        super.onPause()
        applicationContext.sendVideoServiceEvent(VideoEvent.PAUSE)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onResume() {
        super.onResume()
        applicationContext.sendVideoServiceEvent(VideoEvent.RESUME)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onDestroy() {
        super.onDestroy()
        applicationContext.sendVideoServiceEvent(VideoEvent.DESTROY)
        unregisterServiceBroadcastReceiver()
        applicationContext.stopService(Intent(applicationContext, VideoService::class.java))
    }


    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    private fun registerServiceBroadcastReceiver() {
        val receiverFlag = ContextCompat.RECEIVER_EXPORTED
        ContextCompat.registerReceiver(
            applicationContext,
            viewModel.serviceBroadcastReceiver,
            IntentFilter(MainViewModel.SERVICE_BROADCAST_FILTER),
            receiverFlag
        )
    }

    private fun unregisterServiceBroadcastReceiver() {
        applicationContext.unregisterReceiver(viewModel.serviceBroadcastReceiver)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun Context.sendVideoServiceEvent(event: VideoEvent) {
        val intent = Intent().apply {
            setAction(VideoService.CAMERA_INTENT_FILTER)
            putExtra(VideoService.CAMERA_EVENT, event.value)
        }

        this.sendBroadcast(intent)
    }

    enum class VideoEvent(val value: Int) {
        START(0), END(1), PAUSE(2), RESUME(3), DESTROY(4);

        companion object {
            fun valueOf(value: Int): VideoEvent {
                return when(value) {
                    START.value -> START
                    END.value -> END
                    PAUSE.value -> PAUSE
                    RESUME.value -> RESUME
                    DESTROY.value -> DESTROY
                    else -> START
                }
            }
        }
    }

    private fun hasRequiredPermissions(): Boolean {
        return CAMERAX_PERMISSIONS.all {
            ContextCompat.checkSelfPermission(
                applicationContext,
                it
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    companion object {
        private val CAMERAX_PERMISSIONS = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO
        )
    }
}
