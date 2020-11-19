package magicmarbles.ui

import io.ktor.http.cio.websocket.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import magicmarbles.api.game.Game
import magicmarbles.api.game.GameFactory
import magicmarbles.impl.settings.ExtendedSettings
import magicmarbles.impl.settings.ExtendedSettingsImpl
import magicmarbles.ui.dto.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList

class GameServer(private val gameFactory: GameFactory<ExtendedSettings>) {

    enum class MessageType(val typename: String) { Hover("hover"), State("state"), Move("move") }

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
            socket.send(messageJsonStringOf(MessageType.State, game.toDto()))
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
        sendAllPlayerConnections(id, messageJsonStringOf(MessageType.State, game.toDto()))
    }

    suspend fun restartGame(id: String) {
        val game = activeGames[id] ?: return
        game.restart()
    }

    suspend fun move(id: String, coordinate: CoordinateDto) {
        val game = activeGames[id] ?: return
        val moveRes = game.move(coordinate.column, coordinate.row)
        sendAllPlayerConnections(id, messageJsonStringOf(MessageType.State, game.toDto()))
    }

    suspend fun hover(socket: WebSocketSession, id: String, coordinate: CoordinateDto) {
        val game = activeGames[id] ?: return
        val connectedMarbles = game.field
            .getConnectedMarbles(coordinate.column, coordinate.row)
            ?.map { CoordinateDto(it.first, it.second) } ?: return
        socket.send(messageJsonStringOf(MessageType.Hover, HoverDto(connectedMarbles)))
    }

    private inline fun <reified T> messageJsonStringOf(type: MessageType, payload: T): String =
        Json.encodeToString(MessageDto(type.typename, Json { encodeDefaults = true }.encodeToJsonElement(payload)))

    private fun Game.toDto(): GameStateDto {
        val colorList = this.field.field
            .map { column ->
                column.map { it?.color?.hex?.let { color -> MarbleDto(color) } }
            }

        return GameStateDto(colorList, this.points)
    }

    private suspend fun sendAllPlayerConnections(id: String, message: String) {
        val connections = activeConnections[id] ?: return
        connections.forEach {
            it.send(message)
        }
    }
}