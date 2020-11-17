package magicmarbles.impl

interface Configuration {
    operator fun <T> get(key: String): T
}