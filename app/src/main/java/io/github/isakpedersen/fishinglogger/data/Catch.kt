package io.github.isakpedersen.fishinglogger.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/** One caught fish — when, where, what species, on which lure. */
@Entity(
    indices = [
        Index(value = ["timestamp"], unique = true),
        Index(value = ["lureVariantId"]),
    ],
    foreignKeys = [
        ForeignKey(
            entity = LureVariant::class,
            parentColumns = ["id"],
            childColumns = ["lureVariantId"],
        ),
    ],
)
data class Catch(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    /** Epoch in epoch SECONDS (not millis). Cross-device unique ID (hence the unique index). */
    val timestamp: Long,
    val species: String?,
    /** Grams */
    val weight: Int?,
    val lat: Double?,
    val lon: Double?,
    val lureVariantId: Long?,
    val rig: Rig?,
    /** Phone-only — never travels to or from the watch. The export parser writes lure failures here. */
    val notes: String?,
)

/** Constant names are persisted as TEXT — renaming after real data exists is a data migration */
enum class Rig { FASTDUPP, GLIDEDUPP, BUNNMEITE }
