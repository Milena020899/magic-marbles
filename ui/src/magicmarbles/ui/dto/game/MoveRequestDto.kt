package magicmarbles.ui.dto.game

import kotlinx.serialization.Serializable

@Serializable
class MoveRequestDto(val coordinates: CoordinateDto, val stateId: Int)