package io.github.isakpedersen.fishinglogger.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.isakpedersen.fishinglogger.ui.theme.FishingLoggerTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatchDialogScreen(
    uiState: CatchDialogUiState,
    onBack: () -> Unit,
    onSave: () -> Unit,
    onSpeciesChange: (String) -> Unit,
    onWeightChange: (String) -> Unit,
    onNotesChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("Ny fangst") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Lukk",
                        )
                    }
                },
                actions = {
                    TextButton(
                        onClick = onSave,
                        enabled = !uiState.isSaving && uiState.weightOk,
                    ) {
                        Text("Lagre")
                    }
                },
                windowInsets = WindowInsets(0.dp),
            )
        },
        contentWindowInsets = WindowInsets(0.dp),
    ) { innerPadding ->
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
        ) {
            OutlinedTextField(
                value = uiState.speciesText,
                onValueChange = onSpeciesChange,
                label = { Text("Art") },
                modifier = Modifier.fillMaxWidth(),
                )
            OutlinedTextField(
                value = uiState.weightText,
                onValueChange = onWeightChange,
                label = { Text("Vekt") },
                suffix = { Text("g") },
                isError = !uiState.weightOk,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                )
            OutlinedTextField(
                value = uiState.notesText,
                onValueChange = onNotesChange,
                label = { Text("Notat") },
                minLines = 3,
                modifier = Modifier.fillMaxWidth(),
                )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CatchDialogScreenPreview() {
    FishingLoggerTheme {
        CatchDialogScreen(
            uiState = CatchDialogUiState(),
            onBack = { },
            onSave = { },
            onSpeciesChange = { },
            onWeightChange = { },
            onNotesChange = { },
        )
    }
}
