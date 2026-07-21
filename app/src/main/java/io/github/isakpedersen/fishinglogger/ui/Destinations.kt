package io.github.isakpedersen.fishinglogger.ui

import kotlinx.serialization.Serializable

@Serializable object CatchList
@Serializable data class CatchDetail(val id: Long)
@Serializable object LureCatalog
