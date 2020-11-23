package magicmarbles.api.impl.settings.validator

import magicmarbles.api.impl.Configuration
import magicmarbles.api.impl.settings.ExtendedSettings

class SettingsValidatorImpl(configuration: Configuration) : SettingsValidator {
    private val minFieldSize: Pair<Int, Int> = configuration["minFieldSize"]
    private val remainingMarblePenalty: Pair<Int, Int> = configuration["remainingMarblePenalty"]
    private val minConnectedMarbles: Pair<Int, Int> = configuration["minimumConnectedMarbles"]

    override fun validateSettings(settings: ExtendedSettings): List<String> {
        val errorList = mutableListOf<String>()

        if (settings.width < minFieldSize.first) errorList.add("Width must be at least ${minFieldSize.first}")
        if (settings.height < minFieldSize.second) errorList.add("Height must be at least ${minFieldSize.second}")
        if (settings.remainingMarblePenalty < remainingMarblePenalty.first ||
            settings.remainingMarblePenalty > remainingMarblePenalty.second
        )
            errorList.add("Remaining Marble Penalty must be between ${remainingMarblePenalty.first} and ${remainingMarblePenalty.second}")
        if (settings.minConnectedMarbles < minConnectedMarbles.first || settings.minConnectedMarbles > minConnectedMarbles.second)
            errorList.add("Minimum of connected marbles must be between ${minConnectedMarbles.first} and ${minConnectedMarbles.second}")
        return errorList
    }
}