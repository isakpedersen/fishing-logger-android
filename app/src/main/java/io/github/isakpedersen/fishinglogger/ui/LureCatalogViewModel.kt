package io.github.isakpedersen.fishinglogger.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import io.github.isakpedersen.fishinglogger.FishingLoggerApp
import io.github.isakpedersen.fishinglogger.data.LureDao
import io.github.isakpedersen.fishinglogger.data.LureModelWithVariants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class LureCatalogViewModel(lureDao: LureDao) : ViewModel() {
    val catalog: Flow<List<LureModelWithVariants>> = lureDao.getLureModelsWithVariants()

    private val _expandedModelIds: MutableStateFlow<Set<Long>> = MutableStateFlow(emptySet())
    val expandedModelIds: StateFlow<Set<Long>> = _expandedModelIds.asStateFlow()

    fun toggleModelExpanded(id: Long) {
        _expandedModelIds.update { ids -> if (id in ids) ids - id else ids + id }
    }

    companion object {
        val Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as FishingLoggerApp
                LureCatalogViewModel(app.database.lureDao())
            }
        }
    }
}
