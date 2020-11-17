package magicmarbles.impl.util

import magicmarbles.impl.MapConfig
import magicmarbles.impl.field.FieldImpl
import magicmarbles.impl.game.GameFactoryImpl
import magicmarbles.impl.settings.ExtendedSettingsImpl
import magicmarbles.impl.settings.SettingsValidatorImpl

fun main() {
    val config = MapConfig(
        mapOf(
            "minFieldSize" to Pair(3, 3),
            "remainingMarbleReduction" to Pair(0, 100),
            "minimumConnectedMarbles" to Pair(3, 5)
        )
    )

    val fieldBuilder = TestFieldBuilder(FieldImpl.Factory)
    val extendedSettingsValidator = SettingsValidatorImpl(config)
    val gameFactory = GameFactoryImpl(extendedSettingsValidator, fieldBuilder)
    val settings = ExtendedSettingsImpl(3, 3, 3, { removed -> removed * removed }, 100)
    val game = gameFactory.createGame(settings)
    game?.let {
        game.move(0, 0)
        game.move(2, 0)
        game.move(0, 0)
        game.move(1, 0)
    }

}