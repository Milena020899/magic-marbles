package magicmarbles.ui.dto.settings

import kotlinx.serialization.Serializable

@Serializable
data class SettingsErrorDto(val errors: List<String>)