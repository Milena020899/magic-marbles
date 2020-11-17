package magicmarbles.impl.field

import magicmarbles.api.field.*

class RandomFieldBuilder(private val fieldFactory: ModifiableFieldFactory) : FieldBuilder {
    override fun build(width: Int, height: Int): PlayableField? =
        if (width > 0 && height > 0)
            fieldFactory.createEmptyField(width, height).apply {
                map { _, _ -> Marble(Color.randomColor()) }
            }
        else null
}