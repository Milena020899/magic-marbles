package magicmarbles.impl.settings

data class ExtendedSettingsImpl(
    override val minConnectedMarbles: Int,
    override val pointCalculation: (Int) -> Int,
    override val remainingMarbleReduction: Int,
    override val width: Int,
    override val height: Int
) : ExtendedSettings