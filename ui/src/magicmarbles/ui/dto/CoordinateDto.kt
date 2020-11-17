package magicmarbles.ui.dto

import kotlinx.serialization.Serializable

@Serializable
data class CoordinateDto(val column: Int, val row: Int)