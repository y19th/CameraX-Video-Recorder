package com.example.videoapplication.video_activity_feature.domain.events

import com.example.videoapplication.video_activity_feature.VideoActivity

sealed interface MainEvents {

    data class OnButtonClicked(val onRecordVideo: (VideoActivity.VideoEvent) -> Unit): MainEvents

    data class OnFlashSwitch(val newValue: Boolean): MainEvents
}
