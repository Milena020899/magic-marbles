package magicmarbles.impl.util

fun <A, B, C> Pair<A, B>.mapFirst(transform: (Pair<A, B>) -> C): Pair<C, B> = Pair(transform(this), second)
fun <A, B, C> Pair<A, B>.mapSecond(transform: (Pair<A, B>) -> C): Pair<A, C> = Pair(first, transform(this))