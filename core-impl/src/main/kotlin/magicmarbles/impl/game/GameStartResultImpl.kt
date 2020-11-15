package magicmarbles.impl.game

import magicmarbles.api.game.Failure
import magicmarbles.api.game.Game
import magicmarbles.api.game.Success

data class Success(override val game: Game) : Success
data class Failed(override val errors: List<String>) : Failure