package magicmarbles.ui

import magicmarbles.api.field.FieldException
import magicmarbles.api.game.GameException
import magicmarbles.api.settings.SettingsException
import magicmarbles.ui.dto.SyncDto

open class MarbleGameException : Exception()
class NoGameException : MarbleGameException()
class OutdatedStateException(val syncDto: SyncDto) : MarbleGameException()
class WrappedSettingsException(val settingsException: SettingsException) : MarbleGameException()
class WrappedFieldException(val fieldException: FieldException) : MarbleGameException()
class WrappedGameException(val gameException: GameException) : MarbleGameException()