package magicmarbles.api.field


interface Field {
    val field: List<List<Marble?>>
    val width: Int
    val height: Int
    fun movesPossible(connectedCount: Int): Boolean
    fun isEmpty(): Boolean
    fun marbleCount(): Int
    fun getConnectedMarbles(column: Int, row: Int): List<Pair<Int, Int>>?
    operator fun get(column: Int, row: Int): Marble?
}