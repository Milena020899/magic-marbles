package magicmarbles.ui.dto

import kotlinx.serialization.Serializable

@Serializable
data class GameStateDto(val field: List<List<String>>, val points: Int)