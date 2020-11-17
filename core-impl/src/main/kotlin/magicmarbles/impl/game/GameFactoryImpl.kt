package magicmarbles.impl.game

import magicmarbles.api.field.FieldBuilder
import magicmarbles.api.game.Game
import magicmarbles.api.game.GameFactory
import magicmarbles.impl.settings.ExtendedSettings
import magicmarbles.impl.settings.SettingsValidator

class GameFactoryImpl(
    private val settingsValidator: SettingsValidator,
    private val fieldBuilder: FieldBuilder
) : GameFactory<ExtendedSettings> {
    override fun createGame(settings: ExtendedSettings): Game? {
        val configValidationResult = settingsValidator.validateSettings(settings)
        return if (configValidationResult.isNotEmpty()) null
        else GameImpl(
            { fieldBuilder.build(settings.width, settings.height)!! },
            settings
        )
    }
}