package magicmarbles.impl.game

import magicmarbles.api.field.PlayableField
import magicmarbles.api.game.*
import magicmarbles.impl.settings.ExtendedSettings

class GameImpl(
    private val fieldProvider: () -> PlayableField,
    private val settings: ExtendedSettings
) : Game {
    override var field = fieldProvider()
    override var points: Int = 0
    private var over: Boolean = false

    override fun move(column: Int, row: Int): MoveResult {
        if (over) return GameAlreadyOverImpl
        val removedMarbles = field.move(column, row) ?: return InvalidMoveImpl
        points += settings.pointCalculation(removedMarbles)

        return if (!field.movesPossible()) {
            over = true
            points -= settings.remainingMarbleReduction * field.marbleCount()
            GameOverImpl
        } else ValidMoveImpl
    }

    override fun restart() {
        field = fieldProvider()
        points = 0
        over = false
    }
}

object ValidMoveImpl : ValidMove
object InvalidMoveImpl : InvalidMove
object GameOverImpl : GameOver
object GameAlreadyOverImpl : GameAlreadyOver