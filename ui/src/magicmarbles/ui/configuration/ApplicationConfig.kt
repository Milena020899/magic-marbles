package magicmarbles.ui.configuration

import kotlinx.serialization.Serializable
import magicmarbles.ui.dto.settings.SettingsDto

@Serializable
data class ApplicationConfig(val settingsBounds: SettingsBounds, val defaultSettings: SettingsDto)