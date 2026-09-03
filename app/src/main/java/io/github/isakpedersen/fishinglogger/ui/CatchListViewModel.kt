package io.github.isakpedersen.fishinglogger.ui

import android.database.sqlite.SQLiteConstraintException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import io.github.isakpedersen.fishinglogger.FishingLoggerApplication
import io.github.isakpedersen.fishinglogger.data.Catch
import io.github.isakpedersen.fishinglogger.data.CatchDao
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class CatchListViewModel(private val catchDao: CatchDao) : ViewModel() {
    val catches: Flow<List<Catch>> = catchDao.getAll()

    private val _events: Channel<String> = Channel(Channel.BUFFERED)
    val events: Flow<String> = _events.receiveAsFlow()

    fun addCatch(catch: Catch) {
        viewModelScope.launch {
            try {
                catchDao.insertCatch(catch)
                _events.trySend("La til ${formatCatchLabel(catch)}")
            } catch (_: SQLiteConstraintException) {
                _events.trySend("Kunne ikke legge til fangst")
            }
        }
    }

    fun deleteCatch(id: Long) {
        viewModelScope.launch {
            val rowsDeleted = catchDao.deleteCatch(id)
            if (rowsDeleted > 0) {
                _events.trySend("Slettet fangst")
            } else {
                _events.trySend("Kunne ikke slette fangst")
            }
        }
    }

    companion object {
        val Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as FishingLoggerApplication
                CatchListViewModel(app.database.catchDao())
            }
        }
    }
}
