package io.github.isakpedersen.fishinglogger.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import io.github.isakpedersen.fishinglogger.FishingLoggerApp
import io.github.isakpedersen.fishinglogger.data.LureDao
import io.github.isakpedersen.fishinglogger.data.LureModel
import io.github.isakpedersen.fishinglogger.data.LureModelWithVariants
import io.github.isakpedersen.fishinglogger.data.LureType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LureCatalogViewModel(private val lureDao: LureDao) : ViewModel() {
    val catalog: Flow<List<LureModelWithVariants>> = lureDao.getLureModelsWithVariants()

    private val _expandedModelIds: MutableStateFlow<Set<Long>> = MutableStateFlow(emptySet())
    val expandedModelIds: StateFlow<Set<Long>> = _expandedModelIds.asStateFlow()

    fun toggleModelExpanded(id: Long) {
        _expandedModelIds.update { ids -> if (id in ids) ids - id else ids + id }
    }

    fun addModel(type: LureType, name: String, brand: String?) {
        val model = LureModel(type = type, name = name, brand = brand)
        viewModelScope.launch {
            val id = lureDao.insertLureModel(model)
            _expandedModelIds.update { it + id }
        }
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
