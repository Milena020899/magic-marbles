package magicmarbles.ui.dto

import kotlinx.serialization.Serializable

@Serializable
data class Message(val type: String, val payload: String)