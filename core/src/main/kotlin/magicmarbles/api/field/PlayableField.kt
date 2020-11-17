package magicmarbles.api.field

interface PlayableField : Field {
    fun move(column: Int, row: Int): Int?
}
