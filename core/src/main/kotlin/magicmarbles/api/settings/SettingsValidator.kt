package magicmarbles.api.settings

interface SettingsValidator<TSettings : Settings> {
    fun validateSettings(settings: TSettings): List<String>
}