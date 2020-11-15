package magicmarbles.api.game

import magicmarbles.api.field.Field

interface GameSate
interface ValidGameState : GameSate {
    val field: Field
    val points: Int
}

interface SuccessfulMoveState : ValidGameState
interface InvalidMoveState : ValidGameState
interface GameOverState : ValidGameState
interface InvalidGameState : GameSate {
    val error: String
}
