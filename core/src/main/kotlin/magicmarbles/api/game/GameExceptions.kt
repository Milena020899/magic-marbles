package magicmarbles.api.game

import magicmarbles.api.field.FieldException

open class GameException : Exception()

open class InvalidMoveException(val fieldException: FieldException) : GameException()
open class GameAlreadyOverException : GameException()