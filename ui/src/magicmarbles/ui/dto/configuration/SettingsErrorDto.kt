package magicmarbles.ui.dto.configuration

import kotlinx.serialization.Serializable
import magicmarbles.ui.dto.game.GameStateDto

sealed class StartResult

@Serializable
data class SettingsErrorDto(val errors: List<String>) : StartResult()

data class GameStartedDto(val gameStateDto: GameStateDto) : StartResult()