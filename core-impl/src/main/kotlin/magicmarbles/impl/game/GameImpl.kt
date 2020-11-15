package magicmarbles.impl.game

import magicmarbles.api.field.FieldBuilder
import magicmarbles.api.field.MoveableField
import magicmarbles.api.game.Game
import magicmarbles.api.game.MoveResult
import magicmarbles.impl.settings.ExtendedSettings

class GameImpl(
    private val fieldBuilder: FieldBuilder,
    private val settings: ExtendedSettings
) : Game {

    private var field: MoveableField = createField()
    private var points: Int = 0
    private var over: Boolean = false


    override fun move(column: Int, row: Int): MoveResult {
        if (over) return GameAlreadyOverImpl(field, points)
        val removedMarbles = field.move(column, row) ?: return InvalidMoveImpl(field, points)
        points += settings.pointCalculation(removedMarbles)

        return if (!field.movesPossible(settings.minConnectedMarbles)) {
            over = true
            points -= settings.remainingMarbleReduction * field.marbleCount()
            GameOverImpl(field, points)
        } else {
            ValidMoveImpl(field, points)
        }
    }

    private fun createField(): MoveableField = fieldBuilder.build(settings.width, settings.height)

    override fun restart() {
        field = createField()
        points = 0
        over = false
    }
}