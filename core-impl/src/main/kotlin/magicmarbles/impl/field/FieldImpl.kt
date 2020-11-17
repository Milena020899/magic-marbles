package magicmarbles.impl.field

import magicmarbles.api.field.*

class FieldImpl private constructor(
    override val field: MutableList<MutableList<Marble?>>,
    override val width: Int,
    override val height: Int,
) : Field, ModifiableField, PlayableField {

    override fun move(column: Int, row: Int): Int? {
        if (!checkBounds(column, row)) return null
        val marblesToRemove = getConnectedMarbles(column, row)
        if (marblesToRemove == null || marblesToRemove.size == 1) return null

        marblesToRemove.forEach { (_r, _c) -> this[_r, _c] = null }

        field.forEach { it.removeAndAppendFront({ marble -> marble == null }, { null }) }
        field.removeAndAppendFront({ it.all { marble -> marble == null } }, { MutableList(height) { null } })
        return marblesToRemove.size
    }

    private fun <A> MutableList<A>.removeAndAppendFront(predicate: (A) -> Boolean, creator: () -> A) {
        val removeCount = count(predicate)
        removeAll(predicate)
        addAll(0, List(removeCount) { creator() })
    }

    companion object Factory : ModifiableFieldFactory {
        override fun createEmptyField(width: Int, height: Int): ModifiableField =
            FieldImpl(
                MutableList(width) { MutableList(height) { null } },
                width, height
            )
    }
}