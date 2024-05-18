package com.example.videoapplication.presentation.viewmodels

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import androidx.lifecycle.ViewModel
import com.example.videoapplication.MainActivity
import com.example.videoapplication.R
import com.example.videoapplication.domain.events.MainEvents
import com.example.videoapplication.domain.models.RecordState
import com.example.videoapplication.domain.state.MainState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class MainViewModel : ViewModel() {

    companion object {
        const val RECORD_TIME_PARAM = "record_time"
        const val SERVICE_BROADCAST_FILTER = "service_filter"
    }


    private val _state = MutableStateFlow(MainState())
    val state = _state.asStateFlow()

    //create states because too many receives
    private val _recordTimeState = MutableStateFlow(0L)
    val recordTime = _recordTimeState.asStateFlow()

    val serviceBroadcastReceiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent?) {
            if(intent != null && intent.hasExtra(RECORD_TIME_PARAM)) {
                _recordTimeState.update {
                    val nanosec = intent.getLongExtra(RECORD_TIME_PARAM, recordTime.value)
                    nanosec / 1_000_000_000
                }
            }
        }
    }


    init {
        _state.update {
            it.copy(
                textList = initList()
            )
        }
    }

    fun onEvent(event: MainEvents) {
        when(event) {
            is MainEvents.OnButtonClicked -> {
                when(state.value.recordState) {
                    RecordState.RECORD -> {
                        if(state.value.textNow.next() == state.value.textList.last()) {
                            scrollText(RecordState.FINISH)
                        } else scrollText(RecordState.RECORD)
                    }
                    RecordState.FINISH -> {
                        event.onRecordVideo.invoke(MainActivity.VideoEvent.END)
                        _state.update { it.copy(isEnded = true) }
                    }
                    RecordState.WAIT -> {
                        scrollText(recordState = RecordState.RECORD)
                        event.onRecordVideo.invoke(MainActivity.VideoEvent.START)
                    }
                }
            }
        }
    }

    private fun Pair<Int, String>.next(): String {
        if(first + 1 < state.value.textList.size) {
            return state.value.textList[first + 1]
        }
        return second
    }


    private fun scrollText(recordState: RecordState) {
        val nextIndex: Int = state.value.textNow.first + 1

        if(nextIndex < state.value.textList.size) {
            _state.update {
                it.copy(
                    textNow = Pair(nextIndex, state.value.textList[nextIndex]),
                    recordState = recordState
                )
            }
        }
    }

    private fun initList(): List<String> {
        val mutableList = mutableListOf<String>()
        mutableList.addAll(ResourcesProvider.getInstance().getStringArray(R.array.video_record_hints))
        return mutableList
    }
}

object ResourcesProvider {
    var instance: Resources? = null

    fun set(context: Context) {
        instance = context.resources
    }

    @JvmName("providerGetInstance")
    fun getInstance(): Resources {
        return instance ?: throw NullPointerException("have no resources provider")
    }
}

/*
* bug: почему-то баг появился с если включить приложение и закрыть его, а потом снова
*      открыть то крашнет из-за камеры
* */


