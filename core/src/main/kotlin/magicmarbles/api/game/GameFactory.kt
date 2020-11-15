package magicmarbles.api.game

import magicmarbles.api.settings.Settings

interface GameFactory<TSettings : Settings> {
    fun createGame(settings: TSettings): GameCreationResult
}