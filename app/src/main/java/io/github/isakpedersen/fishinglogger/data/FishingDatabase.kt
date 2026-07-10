package io.github.isakpedersen.fishinglogger.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [Catch::class, LureVariant::class, LureModel::class],
    version = 1
)
abstract class FishingDatabase : RoomDatabase() {
    abstract fun catchDao(): CatchDao
}