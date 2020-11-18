package magicmarbles.impl.field

import magicmarbles.api.field.*
import magicmarbles.impl.settings.ExtendedSettings

class RandomFieldBuilder(private val fieldFactory: ModifiableFieldFactory) : FieldBuilder<ExtendedSettings> {
    override fun build(settings: ExtendedSettings): PlayableField? =
        if (settings.width > 0 && settings.height > 0)
            fieldFactory.createEmptyField(settings.width, settings.height, settings.minConnectedMarbles).apply {
                map { _, _ -> Marble(Color.randomColor()) }
            }
        else null
}