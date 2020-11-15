package magicmarbles.impl.game

import magicmarbles.api.field.FieldBuilder
import magicmarbles.api.field.MoveableField
import magicmarbles.api.game.Game
import magicmarbles.api.game.GameSate
import magicmarbles.impl.settings.ExtendedSettings

class GameImpl(
    private val fieldBuilder: FieldBuilder,
    private val settings: ExtendedSettings
) : Game<ExtendedSettings> {
    private enum class Status { Uninitialized, Running, Over }

    private var field: MoveableField = fieldBuilder.build(settings.width, settings.height)
    private var points = 0
    private var state = Status.Uninitialized

    private fun checkState(): GameSate? = when (state) {
        Status.Uninitialized -> InvalidGameStateImpl("Game has not been started")
        Status.Over -> InvalidGameStateImpl("Game is already over")
        else -> null
    }

    override fun move(column: Int, row: Int): GameSate {
        val res = checkState()
        if (res != null) return res

        val removedMarbles = field.move(column, row) ?: return InvalidMoveStateImpl(field, points)
        points += settings.pointCalculation(removedMarbles)

        return if (!field.movesPossible(settings.minConnectedMarbles)) {
            state = Status.Over
            points - settings.remainingMarbleReduction * field.marbleCount()
            GameOverStateImpl(field, points)
        } else SuccessfulMoveStateImpl(field, points)
    }

    override fun restart(): GameSate {
        TODO("Not yet implemented")
    }
}