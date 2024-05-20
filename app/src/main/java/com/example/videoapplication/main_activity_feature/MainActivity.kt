package com.example.videoapplication.main_activity_feature

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContract
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.Player.REPEAT_MODE_OFF
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.example.videoapplication.video_activity_feature.VideoActivity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class MainActivity : ComponentActivity() {

    private val launcher = registerForActivityResult(RecordVideo()) { result ->
        Log.w("MainActivityResult", "res of activity: $result")
        if (result != null) _resultStateFlow.update { result }
    }

    private val _resultStateFlow = MutableStateFlow("")
    private val resultState = _resultStateFlow.asStateFlow()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            val state by resultState.collectAsState()
            var showed by rememberSaveable {
                mutableStateOf(false)
            }

            Scaffold { paddingValues ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {

                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Button(
                            onClick = {
                                if (!hasVideoPermissions()) {
                                    requestVideoPermissions()
                                } else {
                                    launcher.launch(0)
                                }
                            }
                        ) {
                            Text(
                                text = "Записать видео"
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))


                        if(state.isNotEmpty()) {
                            Text(
                                text = "Видео записано"
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Button(
                                onClick = {
                                    showed = !showed
                                }
                            ) {
                                Text(text = "Показать видео")
                            }
                        }
                    }
                    if(showed ) {
                        ExoPlayerView(
                            modifier = Modifier.fillMaxSize(),
                            fileUri = state
                        )

                        Icon(
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .offset(x = 8.dp, y = 8.dp)
                                .clip(CircleShape)
                                .clickable { showed = false }
                            ,
                            imageVector = Icons.Default.ArrowBackIosNew,
                            contentDescription = "back button on video player",
                            tint = Color.White
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun ExoPlayerView(
        modifier: Modifier = Modifier,
        fileUri: String
    ) {

        val mediaItem = MediaItem.Builder()
            .setUri(fileUri)
            .build()

        val player = remember(mediaItem) {
            ExoPlayer.Builder(applicationContext)
                .build()
                .also { exoPlayer ->
                    exoPlayer.setMediaItem(mediaItem)
                    exoPlayer.prepare()
                    exoPlayer.playWhenReady = false
                    exoPlayer.repeatMode = REPEAT_MODE_OFF
                }
        }
        AndroidView(
            modifier = modifier,
            factory = {
                PlayerView(it).apply {
                    this.player = player
                }
            }
        )

    }

    private fun requestVideoPermissions() {
        ActivityCompat.requestPermissions(
            this, VideoActivity.CAMERAX_PERMISSIONS, 0
        )
    }

    private fun hasVideoPermissions(): Boolean {
        return VideoActivity.CAMERAX_PERMISSIONS.all {
            ContextCompat.checkSelfPermission(
                applicationContext,
                it
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    companion object {
        const val RECORDED_VIDEO = "recorded_video_uri"
    }
}

class RecordVideo : ActivityResultContract<Int, String?>() {
    override fun createIntent(context: Context, input: Int): Intent {
        return Intent(context, VideoActivity::class.java)
    }

    override fun parseResult(resultCode: Int, intent: Intent?): String? {
        Log.w("MainActivityResult", "resultCode: $resultCode, intent: ${intent?.extras}")
        if (resultCode != Activity.RESULT_OK) return null
        return intent?.getStringExtra(MainActivity.RECORDED_VIDEO)
    }
}