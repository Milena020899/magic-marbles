package magicmarbles.impl.game

import magicmarbles.api.field.FieldBuilder
import magicmarbles.api.game.GameCreationResult
import magicmarbles.api.game.GameFactory
import magicmarbles.api.settings.SettingsValidator
import magicmarbles.impl.settings.ExtendedSettings

class GameFactoryImpl(
    private val settingsValidator: SettingsValidator<ExtendedSettings>,
    private val fieldBuilder: FieldBuilder
) :
    GameFactory<ExtendedSettings> {
    override fun createGame(settings: ExtendedSettings): GameCreationResult {
        val configValidationResult = settingsValidator.validateSettings(settings)
        return if (configValidationResult.isNotEmpty()) Failed(configValidationResult)
        else Success(GameImpl(fieldBuilder, settings))
    }
}