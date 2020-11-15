package magicmarbles.api.field

interface ModifiableField : MoveableField {
    operator fun set(column: Int, row: Int, marble: Marble?)
    fun map(transformer: (Int, Int) -> Marble?)
}

interface ModifiableFieldFactory {
    fun createEmptyField(width: Int, height: Int): ModifiableField
}