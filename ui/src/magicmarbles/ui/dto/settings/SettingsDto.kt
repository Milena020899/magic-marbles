package magicmarbles.ui.dto.settings

import kotlinx.serialization.Serializable

@Serializable
data class SettingsDto(
    val width: Int,
    val height: Int,
    val minimumConnectedMarbles: Int,
    val remainingMarblePenalty: Int
)