package magicmarbles.api.impl.settings.factory

import magicmarbles.api.impl.settings.ExtendedSettings

interface ExtendedSettingsFactory {
    fun build(
        width: Int,
        height: Int,
        minimumConnectedMarbles: Int,
        pointCalculation: (Int) -> Int,
        remainingMarblesPenalty: Int
    ): ExtendedSettings
}