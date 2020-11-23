package magicmarbles.api.impl

class MapConfig(private val map: Map<String, Any>) : Configuration {
    @Suppress("UNCHECKED_CAST")
    override fun <T> get(key: String): T = map[key] as T

    companion object {
        fun configOf(vararg pairs: Pair<String, Any>): MapConfig = MapConfig(mapOf(*pairs))
    }
}