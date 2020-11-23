package magicmarbles.api.impl

interface Configuration {
    operator fun <T> get(key: String): T
}