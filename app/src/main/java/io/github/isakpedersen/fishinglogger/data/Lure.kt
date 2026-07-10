package io.github.isakpedersen.fishinglogger.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/** A lure model, e.g. "(Sluk) Remen Møresilda". */
@Entity
data class LureModel(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val type: LureType,
    val name: String,
    val brand: String?
)

/** Constant names are persisted as TEXT — renaming after real data exists is a data migration. */
enum class LureType { SLUK, SPINNER, WOBBLER, FLUE, MARK }

/** A lure variant, e.g. "(Sluk) Remen Møresilda C/R 10 g". */
@Entity(
    indices = [
        Index(value = ["lureModelId"])
    ],
    foreignKeys = [
        ForeignKey(
            entity = LureModel::class,
            parentColumns = ["id"],
            childColumns = ["lureModelId"]
        )
    ]
)
data class LureVariant(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val lureModelId: Long,
    val color: String?,
    /** Grams. */
    val weight: Double?,
    /** Centimeters. */
    val length: Double?,
    /** Lures are archived instead of deleted so that old catches keep their reference.
     * Archived lures do not show up as an option when logging a catch. */
    val archived: Boolean = false,
)