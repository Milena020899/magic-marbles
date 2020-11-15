package magicmarbles.impl.game

import magicmarbles.api.game.Failure
import magicmarbles.api.game.Game
import magicmarbles.api.game.Success
import magicmarbles.api.settings.Settings

data class Success<TSettings : Settings>(override val game: Game<TSettings>) : Success<TSettings>
data class Failed(override val errors: List<String>) : Failure