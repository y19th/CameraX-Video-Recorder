package com.example.videoapplication.video_activity_feature.domain.state

import com.example.videoapplication.video_activity_feature.domain.models.RecordState

data class MainState(
    val textNow: Pair<Int,String> = Pair(0, ""),
    val textList: List<String> = listOf(),
    val isEnded: Boolean = false,
    val fileUri: String = "",

    val recordState: RecordState = RecordState.WAIT
)
