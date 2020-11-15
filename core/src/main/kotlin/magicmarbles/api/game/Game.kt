package magicmarbles.api.game

interface Game {
    fun move(column: Int, row: Int): MoveResult
    fun restart()
}