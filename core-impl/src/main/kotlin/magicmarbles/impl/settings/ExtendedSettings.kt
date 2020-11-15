package magicmarbles.impl.settings

import magicmarbles.api.settings.Settings

interface ExtendedSettings : Settings {
    val minConnectedMarbles: Int
    val pointCalculation: (Int) -> Int
    val remainingMarbleReduction: Int
}