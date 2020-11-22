package magicmarbles.api.field

open class FieldException : Exception()
open class NotEnoughConnectedMarblesException : FieldException()
open class InvalidCoordinateException(val row: Int, val column: Int) : FieldException()