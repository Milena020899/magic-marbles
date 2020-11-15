package magicmarbles.api.game

import magicmarbles.api.settings.Settings


interface GameCreationResult
interface Success<TSettings : Settings> : GameCreationResult {
    val game: Game<TSettings>
}

interface Failure : GameCreationResult {
    val errors: List<String>
}
