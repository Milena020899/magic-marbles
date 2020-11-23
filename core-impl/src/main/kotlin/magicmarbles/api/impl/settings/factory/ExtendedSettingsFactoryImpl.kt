package magicmarbles.api.impl.settings.factory

import magicmarbles.api.impl.settings.ExtendedSettings
import magicmarbles.api.impl.settings.ExtendedSettingsImpl

class ExtendedSettingsFactoryImpl : ExtendedSettingsFactory {
    override fun build(
        width: Int,
        height: Int,
        minimumConnectedMarbles: Int,
        pointCalculation: (Int) -> Int,
        remainingMarblesPenalty: Int
    ): ExtendedSettings =
        ExtendedSettingsImpl(width, height, minimumConnectedMarbles, pointCalculation, remainingMarblesPenalty)
}