package com.example.videoapplication.presentation.screens

import androidx.camera.view.LifecycleCameraController
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.videoapplication.MainActivity
import com.example.videoapplication.presentation.components.CameraPreviewScreen
import com.example.videoapplication.presentation.viewmodels.MainEvents
import com.example.videoapplication.presentation.viewmodels.MainViewModel
import com.example.videoapplication.presentation.viewmodels.RecordState

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    controller: LifecycleCameraController,
    viewModel: MainViewModel,
    onSheetStateChange: () -> Unit,
    onRecordVideo: (MainActivity.VideoEvent) -> Unit,
) {
    val state by viewModel.state.collectAsState()


    Box(
        modifier = Modifier
            .fillMaxSize()
            .then(modifier)
    ) {
        CameraPreviewScreen(
            lifecycleCameraController = controller,
            modifier = Modifier.fillMaxSize(),
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(
                        bottomStart = 15.dp, bottomEnd = 15.dp, topStart = 0.dp, topEnd = 0.dp
                    )
                )
                .border(
                    width = 1.dp,
                    color = Color.LightGray,
                    shape = RoundedCornerShape(
                        bottomStart = 15.dp, bottomEnd = 15.dp, topStart = 0.dp, topEnd = 0.dp
                    )
                )
                .padding(all = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Text(
                text = "state now: ${state.recordState}"
            )

            Button(
                onClick = {
                    viewModel.onEvent(
                        MainEvents.OnButtonClicked(onRecordVideo)
                    )
                    onSheetStateChange.invoke()
                },
                enabled = !state.isEnded
            ) {
                val buttonText = when(state.recordState) {
                    RecordState.RECORD -> "continue"
                    RecordState.FINISH -> "ended"
                    RecordState.WAIT -> "start"
                }

                Text(
                    text = buttonText
                )
            }
        }
    }
}