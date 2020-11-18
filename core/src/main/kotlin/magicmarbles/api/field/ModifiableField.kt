package magicmarbles.api.field

interface ModifiableField : PlayableField {
    override val field: MutableList<MutableList<Marble?>>

    operator fun set(column: Int, row: Int, marble: Marble?) {
        if (!checkBounds(column, row)) return
        field[column][row] = marble
    }

    fun map(transformer: (Int, Int) -> Marble?) {
        field.indices.forEach { column ->
            field[column].indices.forEach { row ->
                this[column, row] = transformer(column, row)
            }
        }
    }
}

interface ModifiableFieldFactory {
    fun createEmptyField(width: Int, height: Int, minConnected: Int): ModifiableField
}