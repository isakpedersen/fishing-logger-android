package io.github.isakpedersen.fishinglogger

import android.app.Application
import androidx.room.Room
import io.github.isakpedersen.fishinglogger.data.FishingDatabase

class FishingLoggerApplication : Application() {
    val database: FishingDatabase by lazy {
        Room.databaseBuilder(
            this,
            FishingDatabase::class.java,
            "fishing.db",
        ).build()
    }
}
