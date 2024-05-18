package com.example.videoapplication.domain.events

import com.example.videoapplication.MainActivity

sealed interface MainEvents {

    data class OnButtonClicked(val onRecordVideo: (MainActivity.VideoEvent) -> Unit): MainEvents
}
