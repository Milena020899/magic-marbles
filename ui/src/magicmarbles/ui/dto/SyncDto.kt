package magicmarbles.ui.dto

import kotlinx.serialization.Serializable
import magicmarbles.ui.dto.game.GameStateDto
import magicmarbles.ui.dto.settings.SettingsDto

@Serializable
class SyncDto(val settings: SettingsDto?, val gameState: GameStateDto?)