package magicmarbles.api.impl.game

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.mapEither
import com.github.michaelbull.result.onSuccess
import magicmarbles.api.field.PlayableField
import magicmarbles.api.game.Game
import magicmarbles.api.game.GameAlreadyOverException
import magicmarbles.api.game.GameException
import magicmarbles.api.game.InvalidMoveException
import magicmarbles.api.impl.settings.ExtendedSettings

class GameImpl(
    val fieldProvider: () -> PlayableField,
    private val settings: ExtendedSettings
) : Game {
    override var field = fieldProvider()
    override var points: Int = 0
    override var over: Boolean = !field.movesPossible()

    override fun move(column: Int, row: Int): Result<Unit, GameException> =
        if (over) Err(GameAlreadyOverException())
        else field.move(column, row)
            .onSuccess {
                points += settings.pointCalculation(it)
                if (!field.movesPossible()) {
                    over = true
                }
            }.mapEither({ }, { InvalidMoveException(it) })

    override fun restart() {
        field = fieldProvider()
        points = 0
        over = false
    }
}