package magicmarbles.ui

import io.ktor.http.cio.websocket.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import magicmarbles.api.game.*
import magicmarbles.impl.settings.ExtendedSettings
import magicmarbles.impl.settings.ExtendedSettingsImpl
import magicmarbles.impl.settings.SettingsValidator
import magicmarbles.ui.dto.ErrorDto
import magicmarbles.ui.dto.MessageDto
import magicmarbles.ui.dto.configuration.*
import magicmarbles.ui.dto.game.CoordinateDto
import magicmarbles.ui.dto.game.GameStateDto
import magicmarbles.ui.dto.game.HoverResultDto
import magicmarbles.ui.dto.game.MarbleDto
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList

class GameServer(
    private val gameFactory: GameFactory<ExtendedSettings>,
    private val settingsValidator: SettingsValidator
) {

    enum class MessageType(val typename: String) {
        StateSync("stateSync"), //on initial request to push either running game or initial config
        Hover("hover"), //push hover result to requesting socket
        StateUpdate("stateUpdate"), //pushes new game state
        GameStart("start"), //pushes start of a game
        InvalidMove("invalidMove"), //pushes invalid move
        SettingsResult("settingsResult"), //pushes result of a configuration
        GameOver("gameOver"), //pushes game over
        GameAlreadyOver("gameAlreadyOver") //pushes when game is already over
    }

    private val defaultSettings = ExtendedSettingsImpl(5, 5, 3, { it * 2 }, 50)

    private val activeConnections = ConcurrentHashMap<String, MutableList<WebSocketSession>>()
    private val activeGames = ConcurrentHashMap<String, Game>()

    suspend fun connect(id: String, socket: WebSocketSession) {
        val list = activeConnections.computeIfAbsent(id) { CopyOnWriteArrayList() }
        list.add(socket)

        val game = activeGames[id]

        val message = if (game == null) messageOf(
            MessageType.StateSync,
            NoGameSync(defaultSettings.toDto()),
            Json { classDiscriminator = "syncType" })
        else messageOf(MessageType.StateSync, ExistingGameSync(game.toDto()), Json { encodeDefaults = true })

        pushToSocket(message, socket)
    }

    suspend fun startWithConfiguration(websocket: WebSocketSession, id: String, settingsDto: SettingsDto) {
        val validationResult = settingsValidator.validateSettings(settingsDto.toSettings())

        pushToSocket(
            messageOf(
                MessageType.SettingsResult,
                if (validationResult.isEmpty()) ConfigurationSuccess else ConfigurationError(validationResult),
                Json { classDiscriminator = "settingsResult" }),
            websocket
        )

        if (validationResult.isEmpty()) {
            val game = activeGames.computeIfAbsent(id) { gameFactory.createGame(settingsDto.toSettings())!! }
            pushGameStart(id, game)
        }
    }

    private suspend fun pushGameStart(id: String, game: Game) {
        pushToAllPlayerConnections(id, messageOf(MessageType.GameStart, GameStartDto))
        pushToAllPlayerConnections(id, game.toMessage())
    }

    suspend fun restartGame(id: String) {
        val game = activeGames[id] ?: return
        game.restart()
        pushGameStart(id, game)
    }

    suspend fun move(socket: WebSocketSession, id: String, coordinate: CoordinateDto) {
        val game = activeGames[id] ?: return
        when (game.move(coordinate.column, coordinate.row)) {
            is ValidMove -> pushToAllPlayerConnections(id, game.toMessage())
            is GameOver -> pushToAllPlayerConnections(id, game.toMessage(MessageType.GameOver))
            is InvalidMove -> pushToSocket(messageOf(MessageType.InvalidMove, coordinate), socket)
            is GameAlreadyOver -> pushToSocket(
                messageOf(MessageType.GameAlreadyOver, ErrorDto("Game is already over")), socket
            )
        }
    }

    suspend fun hover(socket: WebSocketSession, id: String, coordinate: CoordinateDto) {
        val game = activeGames[id] ?: return
        val connectedMarbles = game.field
            .getConnectedMarbles(coordinate.column, coordinate.row)
            ?.map { CoordinateDto(it.first, it.second) } ?: return
        pushToSocket(messageOf(MessageType.Hover, HoverResultDto(connectedMarbles)), socket)
    }

    private fun Game.toMessage(messageType: MessageType = MessageType.StateUpdate): MessageDto =
        messageOf(messageType, this.toDto(), Json { encodeDefaults = true })

    private inline fun <reified T> messageOf(
        type: MessageType,
        payload: T,
        serializer: Json = Json
    ): MessageDto =
        MessageDto(type.typename, serializer.encodeToJsonElement(payload))

    // TODO remove impl in favor of factory
    // TODO unifying naming
    private fun SettingsDto.toSettings(): ExtendedSettings = ExtendedSettingsImpl(
        width,
        height,
        connectedMarbles,
        defaultSettings.pointCalculation,
        remainingMarbleDeduction
    )

    private fun ExtendedSettings.toDto(): SettingsDto =
        SettingsDto(width, height, minConnectedMarbles, remainingMarbleReduction)

    private fun Game.toDto(): GameStateDto {
        val colorList = this.field.field
            .map { column ->
                column.map { it?.color?.hex?.let { color -> MarbleDto(color) } }
            }
        return GameStateDto(colorList, this.points)
    }

    private suspend fun pushToSocket(messageDto: MessageDto, vararg websockets: WebSocketSession) {
        val messageString = Json.encodeToString(messageDto)
        websockets.forEach {
            it.send(messageString)
        }
    }

    private suspend fun pushToAllPlayerConnections(id: String, messageDto: MessageDto) {
        val connections = activeConnections[id] ?: return
        pushToSocket(messageDto, *connections.toTypedArray())
    }
}