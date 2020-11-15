package magicmarbles.impl.util

import magicmarbles.impl.field.FieldImpl
import magicmarbles.impl.game.GameFactoryImpl
import magicmarbles.impl.game.Success
import magicmarbles.impl.settings.ExtendedSettingsImpl
import magicmarbles.impl.settings.ExtendedSettingsValidator

fun main() {
    val config = MapConfig(
        mapOf(
            "minFieldSize" to Pair(3, 3),
            "remainingMarbleReduction" to Pair(0, 100),
            "minimumConnectedMarbles" to Pair(3, 5)
        )
    )

    val fieldBuilder = TestFieldBuilder(FieldImpl.Factory)
    val extendedSettingsValidator = ExtendedSettingsValidator(config)
    val gameFactory = GameFactoryImpl(extendedSettingsValidator, fieldBuilder)
    val settings = ExtendedSettingsImpl(3, { removed -> removed * removed }, 100, 3, 3)
    val game = gameFactory.createGame(settings)
    if (game is Success) {
        game.game.move(0, 0)
        game.game.move(2, 0)
        game.game.move(0, 0)
        game.game.move(1, 0)
    }

}