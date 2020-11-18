package magicmarbles.ui

import io.ktor.http.cio.websocket.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.encodeToJsonElement
import magicmarbles.api.game.Game
import magicmarbles.api.game.GameFactory
import magicmarbles.impl.settings.ExtendedSettings
import magicmarbles.impl.settings.ExtendedSettingsImpl
import magicmarbles.ui.dto.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList

class GameServer(private val gameFactory: GameFactory<ExtendedSettings>) {

    private val defaultSettings = ExtendedSettingsImpl(5, 5, 3, { it * 2 }, 50)

    private val activeConnections = ConcurrentHashMap<String, MutableList<WebSocketSession>>()
    private val activeGames = ConcurrentHashMap<String, Game>()

    suspend fun connect(id: String, socket: WebSocketSession) {
        val list = activeConnections.computeIfAbsent(id) { CopyOnWriteArrayList() }
        list.add(socket)

        if (list.size == 1) {
            socket.send(
                Json.encodeToString(
                    MessageDto(
                        "settings", Json.encodeToJsonElement(
                            SettingsDto(
                                defaultSettings.width,
                                defaultSettings.height,
                                defaultSettings.minConnectedMarbles,
                                defaultSettings.remainingMarbleReduction
                            )
                        )
                    )
                )
            )
        } else {
            val game = activeGames[id] ?: return
            socket.send(Json.encodeToString(MessageDto("state", game.stateToJson())))
        }
    }

    suspend fun configureAndStart(id: String, settings: SettingsDto) {
        val userSettings = ExtendedSettingsImpl(
            settings.width,
            settings.height,
            settings.connectedMarbles,
            defaultSettings.pointCalculation,
            settings.remainingMarbleDeduction
        )
        val game = activeGames.computeIfAbsent(id) { gameFactory.createGame(userSettings)!! }
        sendMessageToPlayer(id, "state", game.stateToJson())
    }

    suspend fun restartGame(id: String) {
        val game = activeGames[id] ?: return
        game.restart()

    }

    suspend fun move(id: String, coordinate: CoordinateDto) {

    }

    suspend fun hover(id: String, coordinate: CoordinateDto) {
        val game = activeGames[id] ?: return
        val connectedMarbles = game.field.getConnectedMarbles(coordinate.column, coordinate.row) ?: return
        sendMessageToPlayer(
            id,
            "hover",
            Json.encodeToJsonElement(HoverDto(connectedMarbles.map { CoordinateDto(it.first, it.second) }))
        )
    }

    private suspend fun sendMessageToPlayer(id: String, type: String, payload: JsonElement) {
        val message = Json.encodeToString(MessageDto(type, payload))
        forEachConnection(id) { it.send(message) }
    }

    private fun Game.stateToJson(): JsonElement {
        val colorList = this.field.field
            .map { column ->
                column.map { it?.color?.hex?.let { color -> MarbleDto(color) } }
            }

        return Json(builderAction = { encodeDefaults = true }).encodeToJsonElement(GameStateDto(colorList, this.points))
    }

    private suspend fun forEachConnection(id: String, func: suspend (WebSocketSession) -> Unit) {
        val connections = activeConnections[id] ?: return
        connections.forEach {
            func(it)
        }
    }
}