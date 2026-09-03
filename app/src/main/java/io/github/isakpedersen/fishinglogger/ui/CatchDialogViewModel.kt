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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant

data class CatchDialogUiState(
    val isSaving: Boolean = false,
    val speciesText: String = "",
    val weightText: String = "",
    val notesText: String = "",
) {
    val species get() = speciesText.trim().takeIf { it.isNotBlank() }
    val weight get() = weightText.toIntOrNull()
    val notes get() = notesText.trim().takeIf { it.isNotBlank() }
    val weightOk get() = weightText.isBlank() || weight != null
}

class CatchDialogViewModel(private val catchDao: CatchDao) : ViewModel() {
    private val timestamp: Long = Instant.now().epochSecond

    private val _uiState: MutableStateFlow<CatchDialogUiState> =
        MutableStateFlow(CatchDialogUiState())
    val uiState: StateFlow<CatchDialogUiState> = _uiState.asStateFlow()

    private val _events: Channel<String> = Channel(Channel.BUFFERED)
    val events: Flow<String> = _events.receiveAsFlow()

    fun onSpeciesChange(species: String) =
        _uiState.update { it.copy(speciesText = species) }

    fun onWeightChange(weight: String) =
        _uiState.update { it.copy(weightText = weight.filter(Char::isDigit)) }

    fun onNotesChange(notes: String) =
        _uiState.update { it.copy(notesText = notes) }

    fun save(onSaved: () -> Unit) {
        _uiState.update { it.copy(isSaving = true) }

        val catch = Catch(
            timestamp = timestamp,
            species = _uiState.value.species,
            weight = _uiState.value.weight,
            lat = null,
            lon = null,
            lureVariantId = null,
            rig = null,
            notes = _uiState.value.notes,
        )

        viewModelScope.launch {
            try {
                catchDao.insertCatch(catch)
                onSaved()
            } catch (_: SQLiteConstraintException) {
                _uiState.update { it.copy(isSaving = false) }
                _events.trySend("Kunne ikke legge til fangst")
            }
        }
    }

    companion object {
        val Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as FishingLoggerApplication
                CatchDialogViewModel(app.database.catchDao())
            }
        }
    }
}
