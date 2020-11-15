package magicmarbles.impl.settings

data class ExtendedSettingsImpl(
    override val width: Int,
    override val height: Int,
    override val minConnectedMarbles: Int,
    override val pointCalculation: (Int) -> Int,
    override val remainingMarbleReduction: Int
) : ExtendedSettings