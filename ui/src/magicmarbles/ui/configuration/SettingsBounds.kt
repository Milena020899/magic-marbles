package magicmarbles.ui.configuration

import kotlinx.serialization.Serializable
import magicmarbles.api.impl.Configuration
import magicmarbles.api.impl.MapConfig

@Serializable
data class Limit(val lowerLimit: Int, val upperLimit: Int) {
    fun toPair(): Pair<Int, Int> = Pair(lowerLimit, upperLimit)
}

@Serializable
data class SettingsBounds(
    val minFieldSize: Limit,
    val remainingMarblePenalty: Limit,
    val minimumConnectedMarbles: Limit
) {
    fun toConfig(): Configuration = MapConfig
        .configOf(
            "minFieldSize" to minFieldSize.toPair(),
            "remainingMarblePenalty" to remainingMarblePenalty.toPair(),
            "minimumConnectedMarbles" to minimumConnectedMarbles.toPair()
        )
}