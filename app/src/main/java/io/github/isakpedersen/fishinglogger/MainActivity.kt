package io.github.isakpedersen.fishinglogger

import android.os.Bundle
import android.util.Log
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
import androidx.lifecycle.lifecycleScope
import io.github.isakpedersen.fishinglogger.sync.WatchLink
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
                        modifier = Modifier.padding(innerPadding),
                    )
                }
            }
        }

        val watchLink = WatchLink(
            context = this,
            scope = lifecycleScope,
            lureDao = (application as FishingLoggerApp).database.lureDao(),
            onStatus = { status = it },
            onMessage = { Log.d("FishingLogger", it.toString()) },
        )
        watchLink.start()
    }
}

@Composable
fun StatusScreen(status: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
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
