package magicmarbles.ui

import io.ktor.http.cio.websocket.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import magicmarbles.api.game.Game
import magicmarbles.api.game.GameFactory
import magicmarbles.impl.settings.ExtendedSettings
import magicmarbles.impl.settings.ExtendedSettingsImpl
import magicmarbles.ui.dto.SettingsDto
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList

class GameServer(private val gameFactory: GameFactory<ExtendedSettings>) {

    private val defaultSettings = ExtendedSettingsImpl(10, 10, 3, { it * 2 }, 50)

    private val activeConnections = ConcurrentHashMap<String, MutableList<WebSocketSession>>()
    private val activeGames = ConcurrentHashMap<String, Game>()

    suspend fun connect(id: String, socket: WebSocketSession) {
        val list = activeConnections.computeIfAbsent(id) { CopyOnWriteArrayList() }
        list.add(socket)

        if (list.size == 1) {
            socket.send(
                Frame.Text(
                    Json.encodeToString(
                        SettingsDto(
                            defaultSettings.width,
                            defaultSettings.height,
                            defaultSettings.minConnectedMarbles,
                            defaultSettings.remainingMarbleReduction
                        )
                    )
                )
            )
        }
    }


    suspend fun reconfigure(id: String) {}

    suspend fun startGame(id: String) {}

    suspend fun move(id: String) {}

    suspend fun hover(id: String) {}
}