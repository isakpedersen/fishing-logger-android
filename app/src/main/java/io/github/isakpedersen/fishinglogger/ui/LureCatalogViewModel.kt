package io.github.isakpedersen.fishinglogger.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import io.github.isakpedersen.fishinglogger.FishingLoggerApp
import io.github.isakpedersen.fishinglogger.data.LureDao
import io.github.isakpedersen.fishinglogger.data.LureModelWithVariants
import kotlinx.coroutines.flow.Flow

class LureCatalogViewModel(lureDao: LureDao) : ViewModel() {
    val catalog: Flow<List<LureModelWithVariants>> = lureDao.getLureModelsWithVariants()

    companion object {
        val Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as FishingLoggerApp
                LureCatalogViewModel(app.database.lureDao())
            }
        }
    }
}
