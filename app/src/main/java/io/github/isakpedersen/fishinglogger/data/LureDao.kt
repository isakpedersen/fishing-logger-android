package io.github.isakpedersen.fishinglogger.data

import androidx.room.Dao
import androidx.room.Query

@Dao
interface LureDao {
    @Query("SELECT * FROM LureModel")
    suspend fun getLureModels(): List<LureModel>

    @Query("SELECT * FROM LureVariant WHERE archived = 0")
    suspend fun getActiveLureVariants(): List<LureVariant>

    @Query("SELECT id FROM LureVariant")
    suspend fun getAllLureVariantIds(): List<Long>
}
