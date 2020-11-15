package magicmarbles.ui

import io.ktor.application.*
import io.ktor.http.cio.websocket.*
import io.ktor.http.content.*
import io.ktor.routing.*
import io.ktor.sessions.*
import io.ktor.util.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.consumeEach
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import magicmarbles.impl.field.FieldImpl
import magicmarbles.impl.field.RandomFieldBuilder
import magicmarbles.impl.game.GameFactoryImpl
import magicmarbles.impl.settings.ExtendedSettingsValidator
import magicmarbles.impl.util.MapConfig
import magicmarbles.ui.dto.Message
import java.time.Duration

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

class MagicMarblesApplication {

    data class GameSession(val id: String)

    val gameServer = GameServer(
        GameFactoryImpl(
            ExtendedSettingsValidator(
                MapConfig(
                    mapOf(
                        "minFieldSize" to Pair(3, 3),
                        "remainingMarbleReduction" to Pair(0, 100),
                        "minimumConnectedMarbles" to Pair(3, 5)
                    )
                )
            ),
            RandomFieldBuilder(FieldImpl.Factory)
        )
    )

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
                            print(frame.data)
                            onReceived(Json.decodeFromString(frame.readText()))
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

    private suspend fun onReceived(message: Message) {
        when (message.type) {
            "reconfigure" -> TODO()
            "startGame" -> TODO()
            "move" -> TODO()
            "hover" -> TODO()
        }
    }
//            get("/") {
//                call.respondText("HELLO WORLD!", contentType = ContentType.Text.Plain)
//            }
//
//            // Static feature. Try to access `/static/ktor_logo.svg`
//            static("/web") {
//                resources("web")
//            }
//
//            get("/session/increment") {
//                val session = call.sessions.get<GameSession>() ?: GameSession()
//                call.sessions.set(session.copy(count = session.count + 1))
//                call.respondText("Counter is ${session.count}. Refresh to increment.")
//            }
//
//            webSocket("/myws/echo") {
//                send(Frame.Text("Hi from server"))
//                while (true) {
//                    val frame = incoming.receive()
//                    if (frame is Frame.Text) {
//                        send(Frame.Text("Client said: " + frame.readText()))
//                    }
//                }
//            }


}

