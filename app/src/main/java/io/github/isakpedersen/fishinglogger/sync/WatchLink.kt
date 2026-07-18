package io.github.isakpedersen.fishinglogger.sync

import android.content.Context
import android.util.Log
import com.garmin.android.connectiq.ConnectIQ
import com.garmin.android.connectiq.IQApp
import com.garmin.android.connectiq.IQDevice
import com.garmin.android.connectiq.exception.InvalidStateException
import com.garmin.android.connectiq.exception.ServiceUnavailableException
import io.github.isakpedersen.fishinglogger.data.CatchDao
import io.github.isakpedersen.fishinglogger.data.LureDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class WatchLink(
    private val context: Context,
    private val scope: CoroutineScope,
    private val lureDao: LureDao,
    private val catchDao: CatchDao,
    private val onStatus: (String) -> Unit,
    private val onEvent: (String) -> Unit,
) {
    private lateinit var connectIQ: ConnectIQ

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

            val status = connectIQ.getDeviceStatus(watch)
            onStatus("${watch.friendlyName}: ${status.name}")

            // Event callbacks deliver IQDevice without friendlyName; use the one from knownDevices.
            connectIQ.registerForDeviceEvents(watch) { _, changedStatus ->
                onStatus("${watch.friendlyName}: ${changedStatus.name}")
            }

            findApp(watch)
        } catch (e: InvalidStateException) {
            onStatus("SDK i ugyldig tilstand")
            Log.e(TAG, "findWatch failed", e)
        } catch (e: ServiceUnavailableException) {
            onStatus("Får ikke kontakt med Garmin Connect")
            Log.e(TAG, "findWatch failed", e)
        }
    }

    private fun findApp(watch: IQDevice) {
        connectIQ.getApplicationInfo(
            WATCH_APP_UUID, watch,
            object : ConnectIQ.IQApplicationInfoListener {
                override fun onApplicationInfoReceived(app: IQApp?) {
                    onStatus("App funnet på klokka")
                }

                override fun onApplicationNotInstalled(applicationId: String?) {
                    onStatus("App ikke installert på klokka")
                }
            },
        )

        connectIQ.registerForAppEvents(watch, IQApp(WATCH_APP_UUID)) { _, _, message, _ ->
            handleMessage(watch, message)
        }
    }

    private fun handleMessage(watch: IQDevice, message: List<Any?>?) {
        val payload = message?.firstOrNull()
        if (payload is Map<*, *>) {
            when (payload["type"]) {
                "lure_request" -> scope.launch { handleLureRequest(watch) }
                "export" -> scope.launch { handleExport(watch = watch, payload = payload) }
                else -> Log.w(TAG, "unhandled watch message: $message")
            }
        } else {
            Log.w(TAG, "unhandled watch message: $message")
        }
    }

    private suspend fun handleLureRequest(watch: IQDevice) {
        val lureModels = lureDao.getLureModels()
        val lureVariants = lureDao.getActiveLureVariants()
        val catalogTree = composeLureCatalog(lureModels, lureVariants)
        val message = mapOf("type" to "lure_catalog", "lures" to catalogTree.map { it.toWire() })

        try {
            connectIQ.sendMessage(watch, IQApp(WATCH_APP_UUID), message) { _, _, status ->
                if (status == ConnectIQ.IQMessageStatus.SUCCESS) {
                    Log.i(TAG, "lure_catalog sent")
                } else {
                    Log.w(TAG, "lure_catalog send failed: ${status.name}")
                }
            }
        } catch (e: InvalidStateException) {
            onStatus("Kunne ikke sende sluker. SDK i ugyldig tilstand")
            Log.w(TAG, "lure_catalog send failed", e)
        } catch (e: ServiceUnavailableException) {
            onStatus("Kunne ikke sende sluker. Får ikke kontakt med Garmin Connect")
            Log.w(TAG, "lure_catalog send failed", e)
        }
    }

    private suspend fun handleExport(watch: IQDevice, payload: Map<*, *>) {
        val entries = payload["entries"] as? List<*> ?: run {
            Log.w(
                TAG,
                "export dropped: 'entries' missing or not a list (was ${payload["entries"]?.javaClass?.simpleName})",
            )
            return
        }

        val knownVariantIds = lureDao.getAllLureVariantIds().toSet()
        val parsedEntries = parseEntries(entries, knownVariantIds)
        val rowIds = catchDao.insertAll(parsedEntries)

        val skipped = entries.size - parsedEntries.size
        val duplicates = rowIds.count { it == -1L }
        val inserted = rowIds.size - duplicates
        Log.i(
            TAG,
            "export: ${entries.size} entries, $inserted inserted, $duplicates duplicates, $skipped skipped",
        )
        onEvent("Lagret $inserted nye fangster")

        val persistedTimestamps = parsedEntries.map { it.timestamp }.toSet()
        /*  The watch compares acked timestamps against its own stored timestamps, so they must go
            back to the watch as the exact same type it was sent as. Therefore, .toLong() is only
            used for the comparison and never for the actual value sent back to the watch.  */
        val timestamps = entries
            .mapNotNull { (it as? Map<*, *>)?.get("timestamp") as? Number }
            .filter { it.toLong() in persistedTimestamps }
        sendExportAck(watch, timestamps)
    }

    private fun sendExportAck(watch: IQDevice, timestamps: List<Number>) {
        val message = mapOf("type" to "export_ack", "timestamps" to timestamps)

        try {
            connectIQ.sendMessage(watch, IQApp(WATCH_APP_UUID), message) { _, _, status ->
                if (status == ConnectIQ.IQMessageStatus.SUCCESS) {
                    Log.i(TAG, "export_ack sent")
                } else {
                    Log.w(TAG, "export_ack send failed: ${status.name}")
                }
            }
        } catch (e: InvalidStateException) {
            Log.w(TAG, "export_ack send failed", e)
        } catch (e: ServiceUnavailableException) {
            Log.w(TAG, "export_ack send failed", e)
        }
    }

    companion object {
        private const val WATCH_APP_UUID = "5710521e23c14252b64384f546ea4d25"
        private const val TAG = "WatchLink"
    }
}
