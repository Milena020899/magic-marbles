package magicmarbles.ui.dto

import kotlinx.serialization.Serializable

@Serializable
data class SettingsDto(val width: Int, val height: Int, val connectedMarbles: Int, val remainingMarbleDeduction: Int)