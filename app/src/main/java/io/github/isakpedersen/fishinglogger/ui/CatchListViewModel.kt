package io.github.isakpedersen.fishinglogger.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import io.github.isakpedersen.fishinglogger.FishingLoggerApplication
import io.github.isakpedersen.fishinglogger.data.Catch
import io.github.isakpedersen.fishinglogger.data.CatchDao
import kotlinx.coroutines.flow.Flow

class CatchListViewModel(catchDao: CatchDao) : ViewModel() {
    val catches: Flow<List<Catch>> = catchDao.getAll()

    companion object {
        val Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as FishingLoggerApplication
                CatchListViewModel(app.database.catchDao())
            }
        }
    }
}
