package io.github.isakpedersen.fishinglogger.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.toRoute
import io.github.isakpedersen.fishinglogger.FishingLoggerApplication
import io.github.isakpedersen.fishinglogger.data.Catch
import io.github.isakpedersen.fishinglogger.data.CatchDao
import io.github.isakpedersen.fishinglogger.data.Lure
import io.github.isakpedersen.fishinglogger.data.LureDao
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

/** A catch bundled with its resolved lure, so the two are always shown in sync. */
data class CatchDetailUiState(
    val catch: Catch?,
    val lure: Lure?,
)

class CatchDetailViewModel(catchDao: CatchDao, lureDao: LureDao, id: Long) : ViewModel() {
    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: Flow<CatchDetailUiState> =
        catchDao.getById(id).flatMapLatest { catch ->
            val variantId = catch?.lureVariantId
            if (variantId == null) {
                flowOf(CatchDetailUiState(catch, lure = null))
            } else {
                lureDao.getLure(variantId).map { lure ->
                    CatchDetailUiState(catch, lure)
                }
            }
        }

    companion object {
        val Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as FishingLoggerApplication
                val id = createSavedStateHandle().toRoute<CatchDetail>().id
                CatchDetailViewModel(app.database.catchDao(), app.database.lureDao(), id)
            }
        }
    }
}
