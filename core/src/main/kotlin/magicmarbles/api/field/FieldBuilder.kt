package magicmarbles.api.field

import com.github.michaelbull.result.Result
import magicmarbles.api.settings.Settings
import magicmarbles.api.settings.SettingsException

interface FieldBuilder<TSettings : Settings> {
    fun build(settings: TSettings): Result<PlayableField, SettingsException>
    fun validate(settings: TSettings): Result<Unit, SettingsException>
}