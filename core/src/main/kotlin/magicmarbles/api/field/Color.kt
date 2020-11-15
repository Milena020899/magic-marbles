package magicmarbles.api.field

enum class Color(val hex: String) {
    RED("#FF0000"),
    GREEN("#00FF00"),
    BLUE("#0000FF");

    companion object {
        fun randomColor(): Color = values().random()
    }
}