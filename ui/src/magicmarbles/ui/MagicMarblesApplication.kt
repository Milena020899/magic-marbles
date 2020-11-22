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
import magicmarbles.api.field.InvalidCoordinateException
import magicmarbles.api.field.NotEnoughConnectedMarblesException
import magicmarbles.api.game.GameAlreadyOverException
import magicmarbles.impl.MapConfig
import magicmarbles.impl.field.FieldImpl
import magicmarbles.impl.game.GameFactoryImpl
import magicmarbles.impl.settings.SettingsValidatorImpl
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
                        .onFailure { call.respond(HttpStatusCode.BadRequest, SettingsErrorDto(it.errors)) }
                }
            }

            post("/restart") {
                withSession { sessionId ->
                    gameServer.restartGame(sessionId)
                        .onSuccess { call.respond(it) }
                        .onFailure { handleNoGameException(call) }
                }
            }

            post("/move") {
                withSession { sessionId ->
                    val coordinates = call.receive<CoordinateDto>()
                    gameServer.move(sessionId, coordinates)
                        .onSuccess { call.respond(it) }
                        .onFailure {
                            when (it) {
                                is NoGameException -> handleNoGameException(call)
                                is WrappedGameException -> when (it.gameException) {
                                    is InvalidCoordinateException -> call.respond(
                                        HttpStatusCode.BadRequest,
                                        "Cannot move on coordinate"
                                    )
                                    is GameAlreadyOverException -> call.respond(
                                        HttpStatusCode.BadRequest,
                                        "Game is already over"
                                    )
                                    else -> defaultExceptionHandler(call)
                                }
                                else -> defaultExceptionHandler(call)
                            }
                        }
                }
            }

            post("/hover") {
                withSession { sessionId ->
                    val coordinates = call.receive<CoordinateDto>()
                    gameServer.hover(sessionId, coordinates)
                        .onSuccess { call.respond(it) }
                        .onFailure {
                            when (it) {
                                is WrappedFieldException -> when (it.fieldException) {
                                    is InvalidCoordinateException -> call.respond(
                                        HttpStatusCode.BadRequest,
                                        "Cannot hover on coordinate"
                                    )
                                    is NotEnoughConnectedMarblesException -> call.respond(
                                        HttpStatusCode.BadRequest,
                                        "Not enough connected marbles"
                                    )
                                }
                            }
                        }
                }
            }

            static {
                defaultResource("index.html", "web")
                resources("web")
            }
        }
    }

//    private suspend fun <T> PipelineContext<Unit, ApplicationCall>.defaultExceptionHandler(
//        result: Result<T, Exception>,
//        function: (Result<T, Exception>) -> Unit
//    ) {
//        if(result is Result<T, NoGameException>)
//
//        result.onFailure {
//            if()
//            if (it is NoGameException) call.respond(HttpStatusCode.NotFound, "No Game found")
//        }
//            .
//
//    }

    private suspend fun handleNoGameException(call: ApplicationCall) =
        call.respond(HttpStatusCode.NotFound, "No game found")

    private suspend fun defaultExceptionHandler(call: ApplicationCall) {
        call.respond(HttpStatusCode.InternalServerError, "Internal error")
    }

    private suspend fun PipelineContext<Unit, ApplicationCall>.withSession(function: suspend (String) -> Unit) {
        val session = call.sessions.get<GameSession>()
        if (session == null) call.respond(status = HttpStatusCode.Unauthorized, "")
        else function(session.id)
    }
}

