package magicmarbles.api.game

import com.github.michaelbull.result.Result
import magicmarbles.api.field.Field

interface Game {
    val field: Field
    val points: Int
    val over: Boolean
    fun move(column: Int, row: Int): Result<Unit, GameException>
    fun restart()
}