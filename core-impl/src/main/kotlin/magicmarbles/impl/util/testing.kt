package magicmarbles.impl.util

import magicmarbles.api.field.impl.FieldImpl
import magicmarbles.impl.GameImpl
import magicmarbles.api.settings.impl.ExtendedSettingsImpl
import magicmarbles.api.settings.impl.ExtendedSettingsValidator

fun main() {
    val config = MapConfig(
        mapOf(
            "minFieldSize" to Pair(3, 3),
            "remainingMarbleReduction" to Pair(0, 100),
            "minimumConnectedMarbles" to Pair(3, 5)
        )
    )

    val fieldFactory = TestFieldBuilder(FieldImpl.Factory)
    val extendedSettingsValidator = ExtendedSettingsValidator(config)
    val game = GameImpl(fieldFactory, extendedSettingsValidator)

    val settings = ExtendedSettingsImpl(-1, { removed -> removed * removed }, 100, 3, 3)
    val x = game.start(settings)
    game.move(0, 0)
    game.move(1, 0)
    game.move(2, 0)
}