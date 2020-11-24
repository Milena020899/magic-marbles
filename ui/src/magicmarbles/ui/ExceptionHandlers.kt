package magicmarbles.ui

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import kotlinx.serialization.SerializationException
import magicmarbles.api.field.FieldException
import magicmarbles.api.field.InvalidCoordinateException
import magicmarbles.api.field.NotEnoughConnectedMarblesException
import magicmarbles.api.game.GameAlreadyOverException
import magicmarbles.api.game.GameException
import magicmarbles.api.game.InvalidMoveException
import magicmarbles.api.settings.SettingsException
import magicmarbles.ui.dto.settings.SettingsErrorDto

suspend fun exceptionHandler(call: ApplicationCall, ex: Exception) {
    when (ex) {
        is MarbleGameException -> handleMarbleExceptions(call, ex)
        is SerializationException -> call.respond(HttpStatusCode.BadRequest, "Invalid parameters")
        else -> defaultExceptionHandler(call)
    }
}

private suspend fun handleMarbleExceptions(call: ApplicationCall, ex: MarbleGameException) {
    when (ex) {
        is OutdatedStateException -> call.respond(HttpStatusCode.Conflict, ex.syncDto)
        is NoGameException -> call.respond(HttpStatusCode.NotFound, "No game found")
        is WrappedGameException -> handleGameException(call, ex.gameException)
        is WrappedFieldException -> handleFieldException(call, ex.fieldException)
        is WrappedSettingsException -> handleSettingsException(call, ex.settingsException)
    }
}

private suspend fun handleGameException(call: ApplicationCall, ex: GameException) {
    when (ex) {
        is GameAlreadyOverException -> call.respond(
            HttpStatusCode.BadRequest,
            "game is already over"
        )
        is InvalidMoveException -> handleFieldException(call, ex.fieldException)
    }
}

private suspend fun handleFieldException(call: ApplicationCall, ex: FieldException) {
    when (ex) {
        is InvalidCoordinateException -> call.respond(
            HttpStatusCode.BadRequest,
            "Invalid coordinates (${ex.column}, ${ex.row})"
        )
        is NotEnoughConnectedMarblesException -> call.respond(
            HttpStatusCode.BadRequest,
            "Not enough connected marbles"
        )
    }
}

private suspend fun handleSettingsException(call: ApplicationCall, ex: SettingsException) =
    call.respond(HttpStatusCode.BadRequest, SettingsErrorDto(ex.errors))

private suspend fun defaultExceptionHandler(call: ApplicationCall) {
    call.respond(HttpStatusCode.InternalServerError, "Internal error")
}