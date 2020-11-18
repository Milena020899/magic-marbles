package magicmarbles.ui.dto

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class MessageDto(val type: String, val payload: JsonElement)