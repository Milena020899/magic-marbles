package magicmarbles.ui.dto

import kotlinx.serialization.Serializable

@Serializable
data class HoverResultDto(val marbles: List<CoordinateDto>)