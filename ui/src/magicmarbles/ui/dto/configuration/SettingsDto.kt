package magicmarbles.ui.dto.configuration

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SettingsDto(val width: Int, val height: Int, val connectedMarbles: Int, val remainingMarbleDeduction: Int)

@Serializable
sealed class ConfigurationResult

@Serializable
@SerialName("error")
data class ConfigurationError(val errors: List<String>) : ConfigurationResult()

@Serializable
@SerialName("success")
object ConfigurationSuccess : ConfigurationResult()