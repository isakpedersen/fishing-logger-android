package io.github.isakpedersen.fishinglogger.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface LureDao {
    @Query("SELECT * FROM LureModel")
    suspend fun getLureModels(): List<LureModel>

    @Query("SELECT * FROM LureVariant WHERE archived = 0")
    suspend fun getActiveLureVariants(): List<LureVariant>

    @Query("SELECT id FROM LureVariant")
    suspend fun getAllLureVariantIds(): List<Long>

    @Transaction
    @Query("SELECT * FROM LureVariant WHERE id = :variantId")
    fun getLure(variantId: Long): Flow<Lure?>

    @Transaction
    @Query("SELECT * FROM LureModel")
    fun getLureModelsWithVariants(): Flow<List<LureModelWithVariants>>

    @Insert
    suspend fun insertLureModel(model: LureModel): Long
}
