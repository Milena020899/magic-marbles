package magicmarbles.ui

import io.ktor.application.*
import io.ktor.http.cio.websocket.*
import io.ktor.http.content.*
import io.ktor.routing.*
import io.ktor.sessions.*
import io.ktor.util.*
import io.ktor.websocket.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.consumeEach
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import magicmarbles.impl.MapConfig
import magicmarbles.impl.field.FieldImpl
import magicmarbles.impl.game.GameFactoryImpl
import magicmarbles.impl.settings.SettingsValidatorImpl
import magicmarbles.impl.util.TestFieldBuilder
import magicmarbles.ui.dto.MessageDto
import magicmarbles.ui.dto.configuration.SettingsDto
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
        ),
        settingsValidator
    )

    @ExperimentalCoroutinesApi
    @KtorExperimentalAPI
    fun Application.main() {
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
            webSocket("/ws") {
                val session = call.sessions.get<GameSession>()
                if (session == null) {
                    close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "No session found"))
                    return@webSocket
                }

                gameServer.connect(session.id, this)

                incoming.consumeEach { frame ->
                    try {
                        if (frame is Frame.Text) {
                            onReceived(session.id, Json.decodeFromString(frame.readText()), this)
                        }
                    } catch (ex: Exception) {
                    }
                }
            }

            static {
                defaultResource("index.html", "web")
                resources("web")
            }
        }
    }

    private suspend fun onReceived(id: String, message: MessageDto, socket: WebSocketSession) {
        when (message.type) {
            "startWithConfiguration" -> {
                val settings = Json.decodeFromJsonElement<SettingsDto>(message.payload)
                gameServer.startWithConfiguration(socket, id, settings)
            }
            "restart" -> gameServer.restartGame(id)
            "move" -> {
                val coordinate = Json.decodeFromJsonElement<CoordinateDto>(message.payload)
                gameServer.move(socket, id, coordinate)
            }
            "hover" -> {
                val coordinate = Json.decodeFromJsonElement<CoordinateDto>(message.payload)
                gameServer.hover(socket, id, coordinate)
            }
        }
    }
}

