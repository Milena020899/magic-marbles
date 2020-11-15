package magicmarbles.impl.field

import magicmarbles.api.field.*

class RandomFieldBuilder(private val fieldFactory: ModifiableFieldFactory) : FieldBuilder {
    override fun build(width: Int, height: Int): ModifiableField =
        fieldFactory.createEmptyField(width, height).apply {
            map { _, _ -> Marble(Color.randomColor()) }
        }
}