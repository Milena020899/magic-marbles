package magicmarbles.impl.util

import com.github.kittinunf.result.Result
import magicmarbles.api.field.*
import magicmarbles.api.settings.SettingsException
import magicmarbles.impl.settings.ExtendedSettings

// TODO delete
class TestFieldBuilder(private val fieldProvider: ModifiableFieldFactory) : FieldBuilder<ExtendedSettings> {
    override fun build(settings: ExtendedSettings): Result<PlayableField, SettingsException> {
        return Result.Success(
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
    }

    override fun validate(settings: ExtendedSettings): Result<Unit, SettingsException> {
        return Result.success(Unit)
    }
}