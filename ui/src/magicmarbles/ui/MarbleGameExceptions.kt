package magicmarbles.ui

import magicmarbles.api.field.FieldException
import magicmarbles.api.game.GameException
import magicmarbles.api.settings.SettingsException

open class MarbleGameException : Exception()
class NoGameException : MarbleGameException()
class WrappedSettingsException(val settingsException: SettingsException) : MarbleGameException()
class WrappedFieldException(val fieldException: FieldException) : MarbleGameException()
class WrappedGameException(val gameException: GameException) : MarbleGameException()