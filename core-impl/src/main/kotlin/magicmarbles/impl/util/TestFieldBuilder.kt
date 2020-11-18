package magicmarbles.impl.util

import magicmarbles.api.field.*
import magicmarbles.impl.settings.ExtendedSettings

class TestFieldBuilder(private val fieldProvider: ModifiableFieldFactory) : FieldBuilder<ExtendedSettings> {
    override fun build(settings: ExtendedSettings): PlayableField {
        return fieldProvider.createEmptyField(settings.width, settings.height, settings.minConnectedMarbles).apply {
            map { column, _ ->
                when (column) {
                    0 -> Marble(Color.RED)
                    1 -> null
                    2 -> Marble(Color.GREEN)
                    else -> Marble(Color.randomColor())
                }
            }
        }
    }
}