package io.github.isakpedersen.fishinglogger.sync

import android.content.Context
import com.garmin.android.connectiq.ConnectIQ

class WatchLink(
    private val context: Context,
    private val onStatus: (String) -> Unit
) {
    private lateinit var connectIQ: ConnectIQ
    private val sdkListener = object : ConnectIQ.ConnectIQListener {
        override fun onSdkReady() {
            onStatus("SDK klar")
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
}