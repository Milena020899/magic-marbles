package magicmarbles.impl.settings

interface SettingsValidator {
    fun validateSettings(settings: ExtendedSettings): List<String>
}