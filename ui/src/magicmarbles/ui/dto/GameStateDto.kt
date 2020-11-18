package magicmarbles.ui.dto

import kotlinx.serialization.Serializable

@Serializable
data class GameStateDto(val field: List<List<MarbleDto?>>, val points: Int)