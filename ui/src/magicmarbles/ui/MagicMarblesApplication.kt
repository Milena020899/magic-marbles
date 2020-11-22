package magicmarbles.ui

import com.github.michaelbull.result.onFailure
import com.github.michaelbull.result.onSuccess
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.http.cio.websocket.*
import io.ktor.http.content.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.serialization.*
import io.ktor.sessions.*
import io.ktor.util.*
import io.ktor.util.pipeline.*
import io.ktor.websocket.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import magicmarbles.api.field.FieldException
import magicmarbles.api.field.InvalidCoordinateException
import magicmarbles.api.field.NotEnoughConnectedMarblesException
import magicmarbles.api.game.GameAlreadyOverException
import magicmarbles.api.game.GameException
import magicmarbles.api.game.InvalidMoveException
import magicmarbles.api.settings.SettingsException
import magicmarbles.impl.MapConfig
import magicmarbles.impl.field.FieldImpl
import magicmarbles.impl.game.GameFactoryImpl
import magicmarbles.impl.settings.validator.SettingsValidatorImpl
import magicmarbles.impl.util.TestFieldBuilder
import magicmarbles.ui.dto.configuration.SettingsDto
import magicmarbles.ui.dto.configuration.SettingsErrorDto
import magicmarbles.ui.dto.game.CoordinateDto
import java.time.Duration

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

class MagicMarblesApplication {

    data class GameSession(val id: String)

    private val settingsValidator = SettingsValidatorImpl(
        MapConfig(
            mapOf(
                "minFieldSize" to Pair(3, 3),
                "remainingMarbleReduction" to Pair(0, 100),
                "minimumConnectedMarbles" to Pair(3, 5)
            )
        )
    )

    private val gameServer = GameServer(
        GameFactoryImpl(
            settingsValidator,
            TestFieldBuilder(FieldImpl.Factory)
        )
    )

    @ExperimentalCoroutinesApi
    @KtorExperimentalAPI
    fun Application.main() {

        install(ContentNegotiation) {
            json(contentType = ContentType.Application.Json)
        }

        install(WebSockets) {
            pingPeriod = Duration.ofSeconds(15)
        }

        install(Sessions) {
            cookie<GameSession>("MAGIC_MARBLE_SESSION")
        }

        intercept(ApplicationCallPipeline.Features) {
            if (call.sessions.get<GameSession>() == null) {
                call.sessions.set(GameSession(generateNonce()))
            }
        }

        routing {
            post("/startWithConfiguration") {
                withSession { sessionId ->
                    val settings = call.receive<SettingsDto>()
                    gameServer.startWithConfiguration(sessionId, settings)
                        .onSuccess { call.respond(it) }
                        .onFailure { exceptionHandler(call, it) }
                }
            }

            post("/restart") {
                withSession { sessionId ->
                    gameServer.restartGame(sessionId)
                        .onSuccess { call.respond(it) }
                        .onFailure { exceptionHandler(call, it) }
                }
            }

            post("/move") {
                withSession { sessionId ->
                    val coordinates = call.receive<CoordinateDto>()
                    gameServer.move(sessionId, coordinates)
                        .onSuccess { call.respond(it) }
                        .onFailure { exceptionHandler(call, it) }
                }
            }

            post("/hover") {
                withSession { sessionId ->
                    val coordinates = call.receive<CoordinateDto>()
                    gameServer.hover(sessionId, coordinates)
                        .onSuccess { call.respond(it) }
                        .onFailure { exceptionHandler(call, it) }
                }
            }

            static {
                defaultResource("index.html", "web")
                resources("web")
            }
        }
    }

    private suspend fun exceptionHandler(call: ApplicationCall, ex: Exception) =
        if (ex is MarbleGameException) handleMarbleExceptions(call, ex)
        else defaultExceptionHandler(call)

    private suspend fun handleMarbleExceptions(call: ApplicationCall, ex: MarbleGameException) {
        when (ex) {
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

    private suspend fun PipelineContext<Unit, ApplicationCall>.withSession(function: suspend (String) -> Unit) {
        val session = call.sessions.get<GameSession>()
        if (session == null) call.respond(status = HttpStatusCode.Unauthorized, "")
        else function(session.id)
    }
}

