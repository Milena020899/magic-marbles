package magicmarbles.ui

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
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import magicmarbles.ui.configuration.ApplicationConfig
import magicmarbles.ui.dto.game.MoveRequestDto
import magicmarbles.ui.dto.settings.SettingsDto
import magicmarbles.ui.util.throwOnFailure
import org.kodein.di.DI
import org.kodein.di.instance
import java.time.Duration

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

class MagicMarblesApplication {

    private val appConfig: ApplicationConfig =
        this::class.java.classLoader.getResource("appconfig.json")?.readText()?.let {
            Json.decodeFromString<ApplicationConfig>(it)
        } ?: throw IllegalArgumentException("Invalid Configuration")

    data class GameSession(val id: String)

    private val di: DI = buildDIContainer(appConfig)


    private val gameServer by di.instance<GameServer>()

    @ExperimentalCoroutinesApi
    @KtorExperimentalAPI
    fun Application.main() {
        install(ContentNegotiation) {
            json(Json { encodeDefaults = true }, ContentType.Application.Json)
        }

        install(DefaultHeaders)

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

        intercept(ApplicationCallPipeline.Call) {
            try {
                this.proceed()
            } catch (ex: Exception) {
                exceptionHandler(call, ex)
            }
        }

        routing {
            post("/sync") {
                withSession { sessionId ->
                    val syncDto = gameServer.sync(sessionId)
                    call.respond(Json { encodeDefaults = false }.encodeToString(syncDto))
                }
            }

            post("/startWithConfiguration") {
                withSession { sessionId ->
                    val settings = call.receive<SettingsDto>()
                    gameServer.startWithConfiguration(sessionId, settings)
                        .onSuccess { call.respond(it) }
                        .throwOnFailure()
                }
            }

            post("/restart") {
                withSession { sessionId ->
                    gameServer.restartGame(sessionId)
                        .onSuccess { call.respond(it) }
                        .throwOnFailure()
                }
            }

            post("/move") {
                withSession { sessionId ->
                    val moveRequest = call.receive<MoveRequestDto>()
                    gameServer.move(sessionId, moveRequest)
                        .onSuccess { call.respond(it) }
                        .throwOnFailure()
                }
            }

            post("/hover") {
                withSession { sessionId ->
                    val hoverRequest = call.receive<MoveRequestDto>()
                    gameServer.hover(sessionId, hoverRequest)
                        .onSuccess { call.respond(it) }
                        .throwOnFailure()
                }
            }

            static {
                defaultResource("index.html", "web")
                resources("web")
            }
        }
    }

    private suspend fun PipelineContext<Unit, ApplicationCall>.withSession(function: suspend (String) -> Unit) {
        val session = call.sessions.get<GameSession>()
        if (session == null) call.respond(status = HttpStatusCode.Unauthorized, "")
        else function(session.id)
    }
}

