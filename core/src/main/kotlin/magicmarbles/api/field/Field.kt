package magicmarbles.api.field


interface Field {
    val field: List<List<Marble?>>
    val width: Int
    val height: Int

    fun checkBounds(column: Int, row: Int) = column in 0 until width && row in 0 until height
    fun movesPossible(): Boolean

    fun marbleCount() = field
        .map { it.count { marble -> marble != null } }
        .sum()

    fun getConnectedMarbles(column: Int, row: Int): List<Pair<Int, Int>>?

    operator fun get(column: Int, row: Int): Marble? = field.getOrNull(column)?.getOrNull(row)

}