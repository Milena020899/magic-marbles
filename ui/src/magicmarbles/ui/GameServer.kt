package magicmarbles.ui

import com.github.michaelbull.result.*
import magicmarbles.api.game.Game
import magicmarbles.api.game.GameFactory
import magicmarbles.api.game.InvalidHoverException
import magicmarbles.api.settings.SettingsException
import magicmarbles.impl.settings.ExtendedSettings
import magicmarbles.impl.settings.ExtendedSettingsImpl
import magicmarbles.ui.dto.configuration.SettingsDto
import magicmarbles.ui.dto.game.CoordinateDto
import magicmarbles.ui.dto.game.GameStateDto
import magicmarbles.ui.dto.game.HoverResultDto
import magicmarbles.ui.dto.game.MarbleDto
import java.util.concurrent.ConcurrentHashMap

class GameServer(
    private val gameFactory: GameFactory<ExtendedSettings>,
) {
    private val defaultSettings = ExtendedSettingsImpl(5, 5, 3, { it * 2 }, 50)
    private val activeGames = ConcurrentHashMap<String, Game>()
    private val storedSettings = ConcurrentHashMap<String, ExtendedSettings>() //TODO integrate

    fun startWithConfiguration(
        id: String,
        settingsDto: SettingsDto
    ): Result<GameStateDto, SettingsException> =
        gameFactory.createGame(settingsDto.toSettings())
            .onSuccess { activeGames[id] = it }
            .map { it.toDto() }

    fun restartGame(id: String): Result<GameStateDto, NoGameException> {
        val game = activeGames[id] ?: return Err(NoGameException())
        game.restart()
        return Ok(game.toDto())
    }

    fun move(id: String, coordinate: CoordinateDto): Result<GameStateDto, MarbleException> {
        val game = activeGames[id] ?: return Err(NoGameException())
        return game.move(coordinate.column, coordinate.row)
            .mapEither({ game.toDto() }, { WrappedGameException(it) })
    }

    //
    fun hover(id: String, coordinate: CoordinateDto): Result<HoverResultDto, MarbleException> {
        val game = activeGames[id] ?: return Err(NoGameException())
        return game.field
            .getConnectedMarbles(coordinate.column, coordinate.row)
            .mapEither(
                { marbles ->
                    HoverResultDto(marbles.map { CoordinateDto(it.first, it.second) })
                }, { WrappedFieldException(it) })
    }

    //
//    // TODO remove impl in favor of factory
//    // TODO unifying naming
    private fun SettingsDto.toSettings(): ExtendedSettings = ExtendedSettingsImpl(
        width,
        height,
        connectedMarbles,
        defaultSettings.pointCalculation,
        remainingMarbleDeduction
    )

    private fun Game.toDto(): GameStateDto {
        val colorList = this.field.field
            .map { column ->
                column.map { it?.color?.hex?.let { color -> MarbleDto(color) } }
            }
        return GameStateDto(colorList, this.points)
    }
}