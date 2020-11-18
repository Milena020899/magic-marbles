package magicmarbles.api.field

import magicmarbles.api.settings.Settings

interface FieldBuilder<TSettings : Settings> {
    fun build(settings: TSettings): PlayableField?
}