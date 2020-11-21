package magicmarbles.ui.dto.game

import kotlinx.serialization.Serializable

@Serializable
data class CoordinateDto(val column: Int, val row: Int)