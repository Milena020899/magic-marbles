package magicmarbles.impl.util

class MapConfig(private val map: Map<String, Any>) : Configuration {
    override fun <T> get(key: String): T = map[key] as T
}