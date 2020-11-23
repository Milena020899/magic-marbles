package magicmarbles.ui

import com.github.michaelbull.result.*
import magicmarbles.api.game.Game
import magicmarbles.api.game.GameFactory
import magicmarbles.api.impl.settings.ExtendedSettings
import magicmarbles.api.impl.settings.ExtendedSettingsImpl
import magicmarbles.api.settings.SettingsException
import magicmarbles.ui.dto.game.CoordinateDto
import magicmarbles.ui.dto.game.GameStateDto
import magicmarbles.ui.dto.game.HoverResultDto
import magicmarbles.ui.dto.game.MarbleDto
import magicmarbles.ui.dto.settings.SettingsDto
import java.util.concurrent.ConcurrentHashMap

class GameServer(
    private val gameFactory: GameFactory<ExtendedSettings>,
    private val defaultSettings: ExtendedSettings
) {
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

    fun move(id: String, coordinate: CoordinateDto): Result<GameStateDto, MarbleGameException> {
        val game = activeGames[id] ?: return Err(NoGameException())
        return game.move(coordinate.column, coordinate.row)
            .mapEither({ game.toDto() }, { WrappedGameException(it) })
    }

    //
    fun hover(id: String, coordinate: CoordinateDto): Result<HoverResultDto, MarbleGameException> {
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
        minimumConnectedMarbles,
        defaultSettings.pointCalculation,
        remainingMarblePenalty
    )

    private fun Game.toDto(): GameStateDto {
        val colorList = this.field.field
            .map { column ->
                column.map { it?.color?.hex?.let { color -> MarbleDto(color) } }
            }
        return GameStateDto(colorList, this.points)
    }
}