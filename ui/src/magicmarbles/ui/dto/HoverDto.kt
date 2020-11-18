package magicmarbles.ui.dto

import kotlinx.serialization.Serializable

@Serializable
data class HoverDto(val marbles: List<CoordinateDto>)