package magicmarbles.api.game


interface GameCreationResult
interface Success : GameCreationResult {
    val game: Game
}

interface Failure : GameCreationResult {
    val errors: List<String>
}
