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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.isakpedersen.fishinglogger.sync.WatchLink
import io.github.isakpedersen.fishinglogger.ui.CatchListScreen
import io.github.isakpedersen.fishinglogger.ui.CatchListViewModel
import io.github.isakpedersen.fishinglogger.ui.theme.FishingLoggerTheme

class MainActivity : ComponentActivity() {
    private var status by mutableStateOf("Starter...")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: CatchListViewModel = viewModel(factory = CatchListViewModel.Factory)
            val catches by viewModel.catches.collectAsStateWithLifecycle(initialValue = emptyList())
            FishingLoggerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(modifier = Modifier.padding(innerPadding)) {
                        StatusScreen(status = status)
                        CatchListScreen(catches = catches, modifier = Modifier.weight(1f))
                    }
                }
            }
        }

        val watchLink = WatchLink(
            context = this,
            scope = lifecycleScope,
            lureDao = (application as FishingLoggerApp).database.lureDao(),
            catchDao = (application as FishingLoggerApp).database.catchDao(),
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
