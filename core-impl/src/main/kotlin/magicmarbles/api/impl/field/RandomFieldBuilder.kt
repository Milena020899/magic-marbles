package magicmarbles.api.impl.field

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.map
import magicmarbles.api.field.*
import magicmarbles.api.impl.settings.ExtendedSettings
import magicmarbles.api.settings.SettingsException

class RandomFieldBuilder(private val fieldFactory: ModifiableFieldFactory) : FieldBuilder<ExtendedSettings> {
    override fun build(settings: ExtendedSettings): Result<PlayableField, SettingsException> =
        validate(settings)
            .map {
                fieldFactory
                    .createEmptyField(settings.width, settings.height, settings.minConnectedMarbles)
                    .apply {
                        map { _, _ -> Marble(Color.randomColor()) }
                    }
            }

    override fun validate(settings: ExtendedSettings): Result<Unit, SettingsException> =
        if (settings.width > 0 && settings.height > 0) {
            Ok(Unit)
        } else Err(SettingsException(listOf("Width and height must be above 0")))
}