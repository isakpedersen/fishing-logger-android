package io.github.isakpedersen.fishinglogger.data

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface LureDao {
    @Query("SELECT * FROM LureModel")
    suspend fun getLureModels(): List<LureModel>

    @Query("SELECT * FROM LureVariant WHERE archived = 0")
    suspend fun getActiveLureVariants(): List<LureVariant>

    @Query("SELECT id FROM LureVariant")
    suspend fun getAllLureVariantIds(): List<Long>

    @Query("SELECT * FROM LureVariant WHERE id = :variantId")
    fun getLure(variantId: Long): Flow<Lure?>
}
