package magicmarbles.api.impl.settings.validator

import magicmarbles.api.impl.settings.ExtendedSettings

interface SettingsValidator {
    fun validateSettings(settings: ExtendedSettings): List<String>
}