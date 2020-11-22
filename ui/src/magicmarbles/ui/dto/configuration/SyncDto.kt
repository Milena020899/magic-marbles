package magicmarbles.ui.dto.configuration

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import magicmarbles.ui.dto.game.GameStateDto

@Serializable
sealed class SyncDto

@SerialName("noGame")
@Serializable
data class NoGameSync(val settingsDto: SettingsDto) : SyncDto()

@SerialName("existingGame")
@Serializable
data class ExistingGameSync(val gameState: GameStateDto) : SyncDto()
