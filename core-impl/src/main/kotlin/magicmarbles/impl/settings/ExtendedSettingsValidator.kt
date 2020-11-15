package magicmarbles.impl.settings

import magicmarbles.api.settings.SettingsValidator
import magicmarbles.impl.util.Configuration

class ExtendedSettingsValidator(private val configuration: Configuration) : SettingsValidator<ExtendedSettings> {

    private val minFieldSize: Pair<Int, Int> = configuration["minFieldSize"]
    private val remainingMarbleReduction: Pair<Int, Int> = configuration["remainingMarbleReduction"]
    private val minConnectedMarbles: Pair<Int, Int> = configuration["minimumConnectedMarbles"]

    override fun validateSettings(settings: ExtendedSettings): List<String> {
        val errorList = mutableListOf<String>()

        if (settings.width < minFieldSize.first) errorList.add("Width must be at least ${minFieldSize.first}")
        if (settings.height < minFieldSize.second) errorList.add("Height must be at least ${minFieldSize.second}")
        if (settings.remainingMarbleReduction < remainingMarbleReduction.first ||
            settings.remainingMarbleReduction > remainingMarbleReduction.second
        ) errorList.add("Remaining Marble Point reduction must be between ${remainingMarbleReduction.first} and ${remainingMarbleReduction.second}")
        if (settings.minConnectedMarbles < minConnectedMarbles.first || settings.minConnectedMarbles > minConnectedMarbles.second)
            errorList.add("Minimum of connected marbles must be between ${minConnectedMarbles.first} and ${minConnectedMarbles.second}")
        return errorList
    }
}