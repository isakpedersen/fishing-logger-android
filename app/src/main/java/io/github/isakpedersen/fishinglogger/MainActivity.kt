package io.github.isakpedersen.fishinglogger

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import io.github.isakpedersen.fishinglogger.ui.theme.FishingLoggerTheme

class MainActivity : ComponentActivity() {
    private var status by mutableStateOf("Starter...")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FishingLoggerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    StatusScreen(
                        status = status,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun StatusScreen(status: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
    ) {
        Text("Status: $status")
    }
}

@Preview(showBackground = true)
@Composable
fun StatusScreenPreview() {
    FishingLoggerTheme {
        StatusScreen("SDK ready")
    }
}