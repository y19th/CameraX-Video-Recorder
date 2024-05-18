package com.example.videoapplication.presentation.viewmodels

import androidx.lifecycle.ViewModel
import com.example.videoapplication.MainActivity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class MainViewModel : ViewModel() {

    private val _state = MutableStateFlow(MainState())
    val state = _state.asStateFlow()

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
        for(i in 0..3) {
            mutableList.add("Item $i index")
        }
        return mutableList
    }

}

sealed interface MainEvents {

    data class OnButtonClicked(val onRecordVideo: (MainActivity.VideoEvent) -> Unit): MainEvents
}

data class MainState(
    val textNow: Pair<Int,String> = Pair(0, ""),
    val textList: List<String> = listOf(),
    val isEnded: Boolean = false,

    val recordState: RecordState = RecordState.WAIT
)

enum class RecordState {
    RECORD, FINISH, WAIT
}