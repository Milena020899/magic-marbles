package magicmarbles.ui.dto

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
data class MessageDto(val type: String, val payload: JsonObject)