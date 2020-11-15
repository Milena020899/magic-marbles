package magicmarbles.api.game

import magicmarbles.api.settings.Settings

interface Game<TSettings : Settings> {
    fun move(column: Int, row: Int): GameSate
    fun restart(): GameSate
}