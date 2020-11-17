package magicmarbles.api.field

interface FieldBuilder {
    fun build(
        width: Int,
        height: Int,
    ): PlayableField?
}