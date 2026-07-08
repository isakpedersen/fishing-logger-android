package io.github.isakpedersen.fishinglogger.sync

import android.content.Context
import android.util.Log
import com.garmin.android.connectiq.ConnectIQ
import com.garmin.android.connectiq.IQApp
import com.garmin.android.connectiq.IQDevice
import com.garmin.android.connectiq.exception.InvalidStateException
import com.garmin.android.connectiq.exception.ServiceUnavailableException

class WatchLink(
    private val context: Context,
    private val onStatus: (String) -> Unit,
    private val onMessage: (Any) -> Unit
) {
    private lateinit var connectIQ: ConnectIQ
    private var device: IQDevice? = null

    private val sdkListener = object : ConnectIQ.ConnectIQListener {
        override fun onSdkReady() {
            onStatus("SDK klar")
            findWatch()
        }

        override fun onInitializeError(errorStatus: ConnectIQ.IQSdkErrorStatus?) {
            onStatus("SDK-feil: ${errorStatus?.name}")
        }

        override fun onSdkShutDown() {
            onStatus("SDK avsluttet")
        }
    }

    fun start() {
        connectIQ = ConnectIQ.getInstance(context, ConnectIQ.IQConnectType.WIRELESS)
        connectIQ.initialize(context, true, sdkListener)
    }

    private fun findWatch() {
        try {
            // Assumes exactly one paired watch; revisit if that ever changes.
            val watch = connectIQ.knownDevices?.firstOrNull()
            if (watch == null) {
                onStatus("Ingen klokke paret")
                return
            }

            device = watch
            val status = connectIQ.getDeviceStatus(watch)
            onStatus("${watch.friendlyName}: ${status.name}")

            // Event callbacks deliver IQDevice without friendlyName; use the one from knownDevices.
            connectIQ.registerForDeviceEvents(watch) { _, changedStatus ->
                onStatus("${watch.friendlyName}: ${changedStatus.name}")
            }

            findApp(watch)
        } catch (e: InvalidStateException) {
            onStatus("SDK i ugyldig tilstand")
            Log.d("WatchLink", "findWatch failed", e)
        } catch (e: ServiceUnavailableException) {
            onStatus("Får ikke kontakt med Garmin Connect")
            Log.d("WatchLink", "findWatch failed", e)
        }
    }

    private fun findApp(watch: IQDevice) {
        connectIQ.getApplicationInfo(WATCH_APP_UUID, watch, object : ConnectIQ.IQApplicationInfoListener {
            override fun onApplicationInfoReceived(app: IQApp?) {
                onStatus("App funnet på klokka")
            }

            override fun onApplicationNotInstalled(applicationId: String?) {
                onStatus("App ikke installert på klokka")
            }
        })

        connectIQ.registerForAppEvents(watch, IQApp(WATCH_APP_UUID)) { _, _, message, _ ->
            onMessage(message)
        }
    }

    companion object {
        private const val WATCH_APP_UUID = "5710521e23c14252b64384f546ea4d25"
    }
}