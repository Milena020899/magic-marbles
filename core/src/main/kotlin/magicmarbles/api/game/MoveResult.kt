package magicmarbles.api.game

import magicmarbles.api.field.Field

interface MoveResult {
    val field: Field
    val points: Int
}

interface ValidMove : MoveResult
interface GameOver : MoveResult
interface InvalidMove : MoveResult
interface GameAlreadyOver : InvalidMove
