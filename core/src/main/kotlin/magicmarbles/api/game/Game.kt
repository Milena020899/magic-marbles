package magicmarbles.api.game

import magicmarbles.api.field.Field

interface Game {
    val field: Field
    val points: Int
    fun move(column: Int, row: Int): MoveResult
    fun restart()
}

interface MoveResult
interface ValidMove : MoveResult
interface GameOver : MoveResult
interface InvalidMove : MoveResult
interface GameAlreadyOver : InvalidMove
