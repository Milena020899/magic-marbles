package magicmarbles.api.impl.settings

import magicmarbles.api.impl.settings.factory.ExtendedSettingsFactory

data class ExtendedSettingsImpl(
    override val width: Int,
    override val height: Int,
    override val minConnectedMarbles: Int,
    override val pointCalculation: (Int) -> Int,
    override val remainingMarblePenalty: Int
) : ExtendedSettings {
    companion object Factory : ExtendedSettingsFactory {
        override fun build(
            width: Int,
            height: Int,
            minimumConnectedMarbles: Int,
            pointCalculation: (Int) -> Int,
            remainingMarblesPenalty: Int
        ) = ExtendedSettingsImpl(width, height, minimumConnectedMarbles, pointCalculation, remainingMarblesPenalty)
    }
}