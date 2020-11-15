package magicmarbles.impl.util

interface Configuration {
    operator fun <T> get(key: String): T
}