package magicmarbles.api.field

interface MoveableField : Field {
    fun move(column: Int, row: Int): Int?
}
