package magicmarbles.impl.game

import magicmarbles.api.field.Field
import magicmarbles.api.game.*

sealed class MoveResultImpl(override val field: Field, override val points: Int) : MoveResult
open class ValidMoveImpl(field: Field, points: Int) : MoveResultImpl(field, points), ValidMove
open class InvalidMoveImpl(field: Field, points: Int) : MoveResultImpl(field, points), InvalidMove
class GameOverImpl(field: Field, points: Int) : ValidMoveImpl(field, points), GameOver
class GameAlreadyOverImpl(field: Field, points: Int) : InvalidMoveImpl(field, points), GameAlreadyOver