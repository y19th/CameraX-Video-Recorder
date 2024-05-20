package com.example.videoapplication.video_activity_feature.presentation.screens

import androidx.camera.core.TorchState
import androidx.camera.view.LifecycleCameraController
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColor
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.FlashlightOff
import androidx.compose.material.icons.filled.FlashlightOn
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.videoapplication.ui.theme.ButtonBlue
import com.example.videoapplication.ui.theme.ButtonGreen
import com.example.videoapplication.ui.theme.ButtonRed
import com.example.videoapplication.ui.theme.RecordRed
import com.example.videoapplication.ui.theme.RecordWhite
import com.example.videoapplication.video_activity_feature.VideoActivity
import com.example.videoapplication.video_activity_feature.domain.events.MainEvents
import com.example.videoapplication.video_activity_feature.domain.models.RecordState
import com.example.videoapplication.video_activity_feature.domain.models.RecordTime
import com.example.videoapplication.video_activity_feature.presentation.components.CameraPreviewScreen
import com.example.videoapplication.video_activity_feature.presentation.components.ContentTextBottomSheet
import com.example.videoapplication.video_activity_feature.presentation.components.FilledButton
import com.example.videoapplication.video_activity_feature.presentation.components.IconButton
import com.example.videoapplication.video_activity_feature.presentation.viewmodels.MainViewModel

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    controller: LifecycleCameraController,
    viewModel: MainViewModel,
    onRecordVideo: (VideoActivity.VideoEvent) -> Unit,
) {
    val state by viewModel.state.collectAsState()

    var expanded by rememberSaveable {
        mutableStateOf(false)
    }

    val hasFlashUnit = rememberSaveable(controller.cameraInfo) {
        if(controller.cameraInfo != null) {
            controller.cameraInfo!!.hasFlashUnit()
        } else false
    }

    val buttonColor by animateColorAsState(
        targetValue = colorByRecordState(state.recordState),
        label = "button_color_animation",
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessMediumLow
        )
    )

    val recordColor by rememberInfiniteTransition(
        label = "recordAnimation"
    ).animateColor(
        initialValue = RecordWhite,
        targetValue = RecordRed,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 1000
            },
            repeatMode = RepeatMode.Reverse
        ), label = "animationColor"
    )

    val buttonText = state.recordState.getStringId()


    Box(
        modifier = Modifier
            .fillMaxSize()
            .then(modifier)
    ) {
        CameraPreviewScreen(
            lifecycleCameraController = controller,
            modifier = Modifier.fillMaxSize(),
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 32.dp, horizontal = 12.dp)
                .align(Alignment.TopCenter),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = Color.White,
                        shape = RoundedCornerShape(15.dp)
                    ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {

                Row(
                    modifier = Modifier
                        .weight(1f)
                        .padding(vertical = 16.dp, horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        modifier = Modifier
                            .background(
                                color = if (state.recordState == RecordState.RECORD)
                                    recordColor else RecordWhite,
                                shape = CircleShape
                            )
                            .padding(all = 8.dp),
                        imageVector = Icons.Default.Videocam,
                        contentDescription = "video_cam"
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    TimeBox(
                        viewModel = viewModel
                    )
                }



                FilledButton(
                    modifier = Modifier
                        .wrapContentWidth()
                        .widthIn(min = 64.dp)
                        .padding(vertical = 20.dp)
                        .zIndex(2f),
                    contentText = stringResource(id = buttonText),
                    shape = RoundedCornerShape(topEnd = 12.dp, bottomEnd = 12.dp),
                    enabled = !state.isEnded,
                    colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
                    onClick = {
                        viewModel.onEvent(
                            MainEvents.OnButtonClicked(onRecordVideo)
                        )
                        expanded = true
                    }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            AnimatedVisibility(
                visible = state.recordState == RecordState.RECORD
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    //horizontalArrangement = Arrangement.Center
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(
                        imageVector = if(state.isFlashOn)
                            Icons.Default.FlashlightOff else Icons.Default.FlashlightOn,
                        enabled = hasFlashUnit,
                        onClick = {
                            val newValue = controller.switchFlashlight()
                            viewModel.onEvent(MainEvents.OnFlashSwitch(newValue))
                        }
                    )

                    SizeBox(viewModel = viewModel)

                    IconButton(
                        imageVector = Icons.Default.CameraAlt,
                        onClick = {
                            /*TODO*/
                        }
                    )
                }
            }
        }

        AnimatedVisibility(
            modifier = Modifier.align(Alignment.BottomCenter),
            visible = expanded,
            enter = slideInVertically { offset -> offset / 2 }
        ) {
            ContentTextBottomSheet(
                textNow = state.textNow.second
            )

        }
    }
}

private fun LifecycleCameraController.switchFlashlight(): Boolean {
    val cameraControl = this.cameraControl
    val torchState = this.cameraInfo?.torchState?.value
    val newValue = torchState == TorchState.OFF
    if(cameraControl != null && torchState != null) {
        cameraControl.enableTorch(newValue)
    }
    return newValue
}

@Composable
fun SizeBox(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel
) {
    val state by viewModel.recordSize.collectAsState()

    Text(
        modifier = modifier
            .background(
                color = Color.White,
                shape = RoundedCornerShape(5.dp)
            )
            .padding(vertical = 4.dp, horizontal = 8.dp)
        ,
        text = "$state mb",
        color = Color.Black,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp
    )
}

@Composable
private fun TimeBox(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel
) {
    val state by viewModel.recordTime.collectAsState()
    val recordState by viewModel.state.collectAsState()
    val time = RecordTime.fromInt(state.toInt())

    val isWait = recordState.recordState == RecordState.WAIT

    Text(
        modifier = modifier,
        text = if(isWait) "Начните запись" else time.toString(),
        color = if(isWait) Color.Black else ButtonBlue,
        fontWeight = FontWeight.SemiBold,
        fontSize = if(isWait) 14.sp else 18.sp
    )

}


private fun colorByRecordState(recordState: RecordState): Color {
    return when (recordState) {
        RecordState.RECORD -> ButtonBlue
        RecordState.FINISH -> ButtonRed
        RecordState.WAIT -> ButtonGreen
    }
}




