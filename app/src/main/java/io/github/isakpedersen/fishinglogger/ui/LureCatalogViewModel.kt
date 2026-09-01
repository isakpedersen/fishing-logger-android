package io.github.isakpedersen.fishinglogger.ui

import android.database.sqlite.SQLiteConstraintException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import io.github.isakpedersen.fishinglogger.FishingLoggerApplication
import io.github.isakpedersen.fishinglogger.data.LureDao
import io.github.isakpedersen.fishinglogger.data.LureModel
import io.github.isakpedersen.fishinglogger.data.LureModelWithVariants
import io.github.isakpedersen.fishinglogger.data.LureType
import io.github.isakpedersen.fishinglogger.data.LureVariant
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LureCatalogViewModel(private val lureDao: LureDao) : ViewModel() {
    val catalog: Flow<List<LureModelWithVariants>> = lureDao.getLureModelsWithVariants()

    private val _expandedModelIds: MutableStateFlow<Set<Long>> = MutableStateFlow(emptySet())
    val expandedModelIds: StateFlow<Set<Long>> = _expandedModelIds.asStateFlow()

    private val _events: Channel<String> = Channel(Channel.BUFFERED)
    val events: Flow<String> = _events.receiveAsFlow()

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

    fun addVariant(lureModelId: Long, color: String?, weight: Double?, length: Double?) {
        val variant = LureVariant(
            lureModelId = lureModelId,
            color = color,
            weight = weight,
            length = length,
        )
        viewModelScope.launch {
            lureDao.insertLureVariant(variant)
        }
    }

    fun deleteModel(modelId: Long, modelName: String) {
        viewModelScope.launch {
            try {
                lureDao.deleteLureModel(modelId)
                _events.trySend("Slettet $modelName")
            } catch (e: SQLiteConstraintException) {
                _events.trySend("Kunne ikke slette $modelName")
            }
        }
    }

    fun deleteVariant(variantId: Long, variantName: String) {
        viewModelScope.launch {
            try {
                lureDao.deleteLureVariant(variantId)
                _events.trySend("Slettet $variantName")
            } catch (e: SQLiteConstraintException) {
                _events.trySend("Kunne ikke slette $variantName")
            }
        }
    }

    companion object {
        val Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as FishingLoggerApplication
                LureCatalogViewModel(app.database.lureDao())
            }
        }
    }
}
