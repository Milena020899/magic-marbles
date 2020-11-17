package magicmarbles.api.field


interface Field {
    val field: List<List<Marble?>>
    val width: Int
    val height: Int

    fun checkBounds(column: Int, row: Int) = column in 0 until width && row in 0 until height

    fun movesPossible(connectedCount: Int): Boolean {
        field.indices.forEach { column ->
            field[column].indices.forEach { row ->
                getConnectedMarbles(column, row)?.size?.let {
                    if (it >= connectedCount) return true
                }
            }
        }

        return false
    }

    fun marbleCount() = field
        .map { it.count { marble -> marble != null } }
        .sum()

    fun getConnectedMarbles(column: Int, row: Int): List<Pair<Int, Int>>? {
        fun getConnectedMarblesInternal(
            color: Color,
            _column: Int,
            _row: Int,
            alreadyFound: MutableList<Pair<Int, Int>>
        ) {
            val marble = this[_column, _row]
            if (marble != null &&
                marble.color == color &&
                Pair(_column, _row) !in alreadyFound
            ) {
                alreadyFound.add(Pair(_column, _row))
                getConnectedMarblesInternal(color, _column - 1, _row, alreadyFound)
                getConnectedMarblesInternal(color, _column + 1, _row, alreadyFound)
                getConnectedMarblesInternal(color, _column, _row + 1, alreadyFound)
                getConnectedMarblesInternal(color, _column, _row - 1, alreadyFound)
            }
        }

        val marble = this[column, row]
        return if (marble == null) null
        else {
            val list = mutableListOf<Pair<Int, Int>>()
            getConnectedMarblesInternal(marble.color, column, row, list)
            list
        }
    }

    operator fun get(column: Int, row: Int): Marble? = field.getOrNull(column)?.getOrNull(row)

}