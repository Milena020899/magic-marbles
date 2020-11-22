package magicmarbles.impl.settings.validator

import magicmarbles.impl.settings.ExtendedSettings

interface SettingsValidator {
    fun validateSettings(settings: ExtendedSettings): List<String>
}