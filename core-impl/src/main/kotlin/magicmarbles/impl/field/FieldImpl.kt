package magicmarbles.impl.field

import magicmarbles.api.field.*

class FieldImpl private constructor(
    override val field: MutableList<MutableList<Marble?>>,
    override val width: Int,
    override val height: Int,
    val minConnected: Int
) : Field, ModifiableField, PlayableField {

    override fun move(column: Int, row: Int): Int? {
        if (!checkBounds(column, row)) return null
        val marblesToRemove = getConnectedMarbles(column, row)
        if (marblesToRemove == null || marblesToRemove.size < minConnected) return null

        marblesToRemove.forEach { (_r, _c) -> this[_r, _c] = null }

        field.forEach { it.removeAndAppendFront({ marble -> marble == null }, { null }) }
        field.removeAndAppendFront({ it.all { marble -> marble == null } }, { MutableList(height) { null } })
        return marblesToRemove.size
    }

    override fun movesPossible(): Boolean {
        field.indices.forEach { column ->
            field[column].indices.forEach { row ->
                getConnectedMarbles(column, row)?.size?.let {
                    if (it >= minConnected) return true
                }
            }
        }

        return false
    }

    override fun getConnectedMarbles(column: Int, row: Int): List<Pair<Int, Int>>? {
        fun getConnectedMarblesInternal(
            color: Color,
            _column: Int,
            _row: Int,
            alreadyFound: MutableList<Pair<Int, Int>>
        ) {
            val marble = this[_column, _row]
            if (marble != null &&
                marble.color == color &&
                Pair(_column, _row) !in alreadyFound
            ) {
                alreadyFound.add(Pair(_column, _row))
                getConnectedMarblesInternal(color, _column - 1, _row, alreadyFound)
                getConnectedMarblesInternal(color, _column + 1, _row, alreadyFound)
                getConnectedMarblesInternal(color, _column, _row + 1, alreadyFound)
                getConnectedMarblesInternal(color, _column, _row - 1, alreadyFound)
            }
        }

        val marble = this[column, row]
        return if (marble == null) null
        else {
            val list = mutableListOf<Pair<Int, Int>>()
            getConnectedMarblesInternal(marble.color, column, row, list)
            if (list.size < minConnected) null
            else list
        }
    }

    private fun <A> MutableList<A>.removeAndAppendFront(predicate: (A) -> Boolean, creator: () -> A) {
        val removeCount = count(predicate)
        removeAll(predicate)
        addAll(0, List(removeCount) { creator() })
    }

    companion object Factory : ModifiableFieldFactory {
        override fun createEmptyField(width: Int, height: Int, minConnected: Int): ModifiableField =
            FieldImpl(
                MutableList(width) { MutableList(height) { null } },
                width, height, minConnected
            )
    }
}