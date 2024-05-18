package com.example.videoapplication.domain.models

import com.example.videoapplication.R

enum class RecordState {
    RECORD, FINISH, WAIT;

    fun getStringId(): Int {
        return when(this) {
            RECORD -> R.string.continue_record
            FINISH -> R.string.finish_record
            WAIT -> R.string.start_record
        }
    }
}