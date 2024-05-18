package com.example.videoapplication

import android.Manifest
import android.annotation.SuppressLint
import android.app.ForegroundServiceStartNotAllowedException
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.content.pm.ServiceInfo
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.camera.core.CameraSelector
import androidx.camera.video.FileOutputOptions
import androidx.camera.video.Recording
import androidx.camera.video.VideoRecordEvent
import androidx.camera.view.CameraController
import androidx.camera.view.video.AudioConfig
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.ServiceCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleService
import com.example.videoapplication.presentation.viewmodels.MainViewModel
import com.example.videoapplication.util.CameraSingleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.io.File

@RequiresApi(Build.VERSION_CODES.O)
class VideoService : LifecycleService() {

    private val channelId = "video_service_channel_id"
    private val serviceId = 13
    private val notificationTitle = "Запись видео"

    private val outputFile get() = File(filesDir, "my-video.mp4")

    private var recording: Recording? = null

    private val coroutineScope = CoroutineScope(Dispatchers.Main.immediate + SupervisorJob())

    private val commandBroadcast = object : BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent?) {
            val cameraEvent = intent?.extras?.getInt(CAMERA_EVENT)

            if (cameraEvent != null) {
                val event = MainActivity.VideoEvent.valueOf(cameraEvent)
                when (event) {
                    MainActivity.VideoEvent.START -> {
                        startCamera()
                    }

                    MainActivity.VideoEvent.END -> {
                        stopCamera()
                    }

                    MainActivity.VideoEvent.PAUSE -> {
                        pauseCamera()
                    }

                    MainActivity.VideoEvent.RESUME -> {
                        resumeCamera()
                    }

                    MainActivity.VideoEvent.DESTROY -> {
                        destroyCamera()
                    }
                }
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.S)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        if (!hasRequiredPermissions()) {
            stopSelf()
        }

        val notification = createNotificationChannel()
            .setContentTitle("Начните запись")
            .setContentText("на кнопку \"Начать\" ")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setAutoCancel(true)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .build()

        try {
            val serviceType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
                ServiceInfo.FOREGROUND_SERVICE_TYPE_CAMERA else 0


            ServiceCompat.startForeground(
                this,
                serviceId,
                notification,
                serviceType
            )

            val receiverFlag = ContextCompat.RECEIVER_EXPORTED

            ContextCompat.registerReceiver(
                applicationContext,
                commandBroadcast,
                IntentFilter(CAMERA_INTENT_FILTER),
                receiverFlag
            )

        } catch (e: ForegroundServiceStartNotAllowedException) {
            Log.w(TAG, "not allow to foreground")
            stopSelf()
        }
        return START_NOT_STICKY
    }

    private fun hasRequiredPermissions(): Boolean {
        return CAMERAX_PERMISSIONS.all {
            ContextCompat.checkSelfPermission(
                applicationContext,
                it
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun pauseCamera() {
        recording?.pause()
    }

    private fun resumeCamera() {
        recording?.resume()
    }

    private fun stopCamera() {
        recording?.stop()
        recording = null
    }

    private fun destroyCamera() {
        CameraSingleton.destroyInstance()
        stopCamera()
        stopSelf()
    }

    override fun onDestroy() {
        super.onDestroy()
        destroyCamera()
    }


    @SuppressLint("MissingPermission")
    private fun startCamera() {

        val controller = CameraSingleton.getInstance(applicationContext).apply {
            setEnabledUseCases(CameraController.VIDEO_CAPTURE or CameraController.IMAGE_ANALYSIS)
            cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            unbind()
            bindToLifecycle(this@VideoService)
        }

        controller.initializationFuture.addListener({
            coroutineScope.launch {
                if(!controller.isRecording) {
                    recording = controller.startRecording(
                        FileOutputOptions.Builder(outputFile).build(),
                        AudioConfig.create(true),
                        ContextCompat.getMainExecutor(applicationContext),
                    ) { event ->
                        when (event) {
                            is VideoRecordEvent.Start -> {
                                sendNotification("Запись видео начата")
                                Log.d(TAG, "recoring started")
                            }

                            is VideoRecordEvent.Pause -> {
                                sendNotification("Запись видео поставлена на паузу. Вернитесь в приложение чтобы продолжить")
                            }

                            is VideoRecordEvent.Resume -> {
                                sendNotification("Запись видео продолжается")
                            }

                            is VideoRecordEvent.Status -> {

                                val intent = Intent().apply {
                                    setAction(MainViewModel.SERVICE_BROADCAST_FILTER)
                                    putExtra(
                                        MainViewModel.RECORD_TIME_PARAM,
                                        event.recordingStats.recordedDurationNanos
                                    )
                                }

                                applicationContext.sendBroadcast(intent)
                            }

                            is VideoRecordEvent.Finalize -> {
                                if (event.hasError()) {
                                    Log.w(TAG, "has error ${event.error}", event.cause)
                                } else {
                                    if(!controller.isRecording) {
                                        sendNotification("Запись успешно сохранена")
                                        Log.d(TAG, "completed video recording")
                                    }
                                    Log.d(TAG, "started successfully")
                                }
                            }
                        }
                    }
                }
            }
        }, ContextCompat.getMainExecutor(applicationContext))
    }

    private fun createNotificationChannel(): NotificationCompat.Builder {
        val channel =
            NotificationChannel(channelId, "video_channel", NotificationManager.IMPORTANCE_LOW)

        getSystemService(NotificationManager::class.java)
            .createNotificationChannel(channel)

        return NotificationCompat.Builder(this, channelId)
    }

    private fun sendNotification(contentText: String) {
        val notification = createNotificationChannel()
            .setContentTitle(notificationTitle)
            .setContentText(contentText)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setAutoCancel(true)
            .build()
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        NotificationManagerCompat.from(applicationContext).notify(12, notification)
    }

    companion object {

        const val CAMERA_EVENT = "camera_event_const"
        const val CAMERA_INTENT_FILTER = "camera_intent_filter"

        private const val TAG = "VideoService"

        private val CAMERAX_PERMISSIONS = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO
        )
    }

}