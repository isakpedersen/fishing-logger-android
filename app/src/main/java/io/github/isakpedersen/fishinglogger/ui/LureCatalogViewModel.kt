package io.github.isakpedersen.fishinglogger.ui

import android.database.sqlite.SQLiteConstraintException
import android.util.Log
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

    fun deleteModel(modelId: Long) {
        viewModelScope.launch {
            try {
                lureDao.deleteLureModel(modelId)
                Log.i("LureCatalogViewModel", "deleted model $modelId") // TODO: replace with snackbar
            } catch (e: SQLiteConstraintException) {
                Log.w("LureCatalogViewModel", "could not delete model $modelId", e) // TODO: replace with snackbar
            }
        }
    }

    fun deleteVariant(variantId: Long) {
        viewModelScope.launch {
            try {
                lureDao.deleteLureVariant(variantId)
                Log.i("LureCatalogViewModel", "deleted variant $variantId") // TODO: replace with snackbar
            } catch (e: SQLiteConstraintException) {
                Log.w("LureCatalogViewModel", "could not delete variant $variantId", e) // TODO: replace with snackbar
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
