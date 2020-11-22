package magicmarbles.impl.util

import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import magicmarbles.api.field.*
import magicmarbles.api.settings.SettingsException
import magicmarbles.impl.settings.ExtendedSettings

// TODO delete
class TestFieldBuilder(private val fieldProvider: ModifiableFieldFactory) : FieldBuilder<ExtendedSettings> {
    override fun build(settings: ExtendedSettings): Result<PlayableField, SettingsException> = Ok(
        fieldProvider.createEmptyField(
            settings.width,
            settings.height,
            settings.minConnectedMarbles
        ).apply {
            map { column, _ ->
                when (column) {
                    0 -> Marble(Color.RED)
                    1 -> null
                    2 -> Marble(Color.GREEN)
                    else -> Marble(Color.randomColor())
                }
            }
        })

    override fun validate(settings: ExtendedSettings): Result<Unit, SettingsException> = Ok(Unit)
}