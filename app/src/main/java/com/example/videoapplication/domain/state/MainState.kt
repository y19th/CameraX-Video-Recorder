package com.example.videoapplication.domain.state

import com.example.videoapplication.domain.models.RecordState

data class MainState(
    val textNow: Pair<Int,String> = Pair(0, ""),
    val textList: List<String> = listOf(),
    val isEnded: Boolean = false,

    val recordState: RecordState = RecordState.WAIT
)
