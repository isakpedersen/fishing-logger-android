package io.github.isakpedersen.fishinglogger.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.toRoute
import io.github.isakpedersen.fishinglogger.FishingLoggerApp
import io.github.isakpedersen.fishinglogger.data.Catch
import io.github.isakpedersen.fishinglogger.data.CatchDao
import kotlinx.coroutines.flow.Flow

class CatchDetailViewModel(catchDao: CatchDao, id: Long) : ViewModel() {
    val catch: Flow<Catch?> = catchDao.getById(id)

    companion object {
        val Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as FishingLoggerApp
                val id = createSavedStateHandle().toRoute<CatchDetail>().id
                CatchDetailViewModel(app.database.catchDao(), id)
            }
        }
    }
}
