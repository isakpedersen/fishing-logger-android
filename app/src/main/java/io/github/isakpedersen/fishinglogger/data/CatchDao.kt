package io.github.isakpedersen.fishinglogger.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CatchDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(catches: List<Catch>): List<Long>

    @Query("SELECT * FROM Catch ORDER BY timestamp DESC")
    fun getAll(): Flow<List<Catch>>
}