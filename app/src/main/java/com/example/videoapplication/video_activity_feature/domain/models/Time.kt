package com.example.videoapplication.video_activity_feature.domain.models

data class RecordTime(
    val minutes: Int,
    val seconds: Int,
    private val isUnspecified: Boolean = false
) {
    companion object {
        val Unspecified: RecordTime = RecordTime(0,0, true)

        fun fromInt(seconds: Int): RecordTime {
            if(seconds == 0) return Unspecified

            val minutes = seconds / 60
            val secondsForTime = seconds % 60

            return RecordTime(
                minutes = minutes,
                seconds = secondsForTime
            )
        }
    }

    fun isUnspecified() = isUnspecified

    override fun toString(): String {
        return if(isUnspecified) {
            "--:--"
        } else {
            if (minutes < 10) {
                if(seconds < 10) {
                    "0$minutes:0$seconds"
                } else {
                    "0$minutes:$seconds"
                }
            } else
                if(seconds < 10) {
                    "$minutes:0$seconds"
                } else {
                    "$minutes:$seconds"
                }
        }
    }
}