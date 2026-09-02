package io.github.isakpedersen.fishinglogger.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
internal fun DeleteDialog(
    label: String,
    onDismiss: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AlertDialog(
        text = {
            Text("Vil du slette $label?")
        },
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onDelete) { Text("Slett") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Avbryt") }
        },
        modifier = modifier,
    )
}
