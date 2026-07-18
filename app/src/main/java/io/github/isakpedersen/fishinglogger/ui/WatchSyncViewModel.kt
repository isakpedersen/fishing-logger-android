package io.github.isakpedersen.fishinglogger.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import io.github.isakpedersen.fishinglogger.FishingLoggerApp
import io.github.isakpedersen.fishinglogger.data.CatchDao
import io.github.isakpedersen.fishinglogger.data.LureDao
import io.github.isakpedersen.fishinglogger.sync.WatchLink
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow

class WatchSyncViewModel(context: Context, lureDao: LureDao, catchDao: CatchDao) : ViewModel() {
    private val _status: MutableStateFlow<String> = MutableStateFlow("Starter...")
    val status: StateFlow<String> = _status.asStateFlow()

    private val _events: Channel<String> = Channel(Channel.BUFFERED)
    val events: Flow<String> = _events.receiveAsFlow()

    private val watchLink = WatchLink(
        context = context,
        scope = viewModelScope,
        lureDao = lureDao,
        catchDao = catchDao,
        onStatus = { _status.value = it },
        onEvent = { _events.trySend(it) },
    )

    init {
        watchLink.start()
    }

    override fun onCleared() {
        watchLink.stop()
        super.onCleared()
    }

    companion object {
        val Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as FishingLoggerApp
                WatchSyncViewModel(
                    context = app,
                    lureDao = app.database.lureDao(),
                    catchDao = app.database.catchDao(),
                )
            }
        }
    }
}
