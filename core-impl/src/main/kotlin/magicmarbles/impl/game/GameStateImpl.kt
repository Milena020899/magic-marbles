package magicmarbles.impl.game

import magicmarbles.api.field.Field
import magicmarbles.api.game.*

sealed class ValidGameStateImpl(override val field: Field, override val points: Int) : ValidGameState
class SuccessfulMoveStateImpl(field: Field, points: Int) : ValidGameStateImpl(field, points), SuccessfulMoveState
class InvalidMoveStateImpl(field: Field, points: Int) : ValidGameStateImpl(field, points), InvalidMoveState
class GameOverStateImpl(field: Field, points: Int) : ValidGameStateImpl(field, points), GameOverState
class InvalidGameStateImpl(override val error: String) : InvalidGameState