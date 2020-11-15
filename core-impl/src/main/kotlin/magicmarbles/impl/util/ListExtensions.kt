package magicmarbles.impl.util

/**
 * Removes the items of the list and
 */
fun <T> MutableList<T>.countRemovedBy(predicate: (T) -> Boolean): Int {
    val predicateCount = count(predicate)
    removeAll(predicate)
    return predicateCount
}

fun <A> MutableList<A>.prepend(count: Int, creator: () -> A) {
    addAll(0, List(count) { creator() })
}

