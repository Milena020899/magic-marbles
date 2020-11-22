package magicmarbles.impl.game

import com.github.michaelbull.result.*
import magicmarbles.api.field.FieldBuilder
import magicmarbles.api.game.Game
import magicmarbles.api.game.GameFactory
import magicmarbles.api.settings.SettingsException
import magicmarbles.impl.settings.ExtendedSettings
import magicmarbles.impl.settings.validator.SettingsValidator

class GameFactoryImpl(
    private val settingsValidator: SettingsValidator,
    private val fieldBuilder: FieldBuilder<ExtendedSettings>
) : GameFactory<ExtendedSettings> {
    override fun createGame(settings: ExtendedSettings): Result<Game, SettingsException> =
        validate(settings)
            .flatMap { fieldBuilder.validate(settings) }
            .map { GameImpl({ fieldBuilder.build(settings).unwrap() }, settings) }

    override fun validate(settings: ExtendedSettings): Result<Unit, SettingsException> =
        settingsValidator.validateSettings(settings).let {
            if (it.isEmpty()) Ok(Unit)
            else Err(SettingsException(it))
        }
}