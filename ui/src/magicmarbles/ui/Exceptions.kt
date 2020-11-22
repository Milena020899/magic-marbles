package magicmarbles.ui

import magicmarbles.api.field.FieldException
import magicmarbles.api.game.GameException

open class MarbleException : Exception()
class NoGameException : MarbleException()
class WrappedFieldException(val fieldException: FieldException) : MarbleException()
class WrappedGameException(val gameException: GameException) : MarbleException()