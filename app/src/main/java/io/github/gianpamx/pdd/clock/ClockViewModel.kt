package io.github.gianpamx.pdd.clock

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ClockViewModel @Inject constructor() : ViewModel() {
    val viewState = flow {
        emit("24:00")
    }.asLiveData(viewModelScope.coroutineContext)
}
