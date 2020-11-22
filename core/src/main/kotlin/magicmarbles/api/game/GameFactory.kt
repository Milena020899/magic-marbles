package magicmarbles.api.game

import com.github.michaelbull.result.Result
import magicmarbles.api.settings.Settings
import magicmarbles.api.settings.SettingsException

interface GameFactory<TSettings : Settings> {
    fun createGame(settings: TSettings): Result<Game, SettingsException>
    fun validate(settings: TSettings): Result<Unit, SettingsException>
}