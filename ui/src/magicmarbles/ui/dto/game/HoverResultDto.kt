package magicmarbles.ui.dto.game

import kotlinx.serialization.Serializable

@Serializable
data class HoverResultDto(val marbles: List<CoordinateDto>)