package magicmarbles.impl.field

import com.github.michaelbull.result.*
import magicmarbles.api.field.*

class FieldImpl private constructor(
    override val field: MutableList<MutableList<Marble?>>,
    override val width: Int,
    override val height: Int,
    private val minConnected: Int
) : Field, ModifiableField, PlayableField {

    override fun move(column: Int, row: Int): Result<Int, FieldException> {
        if (!checkBounds(column, row)) return Err(InvalidCoordinateException(column, row))
        return getConnectedMarbles(column, row)
            .onSuccess { marblesToRemove ->
                if (marblesToRemove.isEmpty())
                    return Err(NotEnoughConnectedMarblesException())

                marblesToRemove.forEach { (_r, _c) -> this[_r, _c] = null }

                field.forEach { it.removeAndAppendFront({ marble -> marble == null }, { null }) }
                field.removeAndAppendFront({ it.all { marble -> marble == null } }, { MutableList(height) { null } })
            }.map { it.size }
    }

    override fun movesPossible(): Boolean {
        field.indices.forEach { column ->
            field[column].indices.forEach { row ->
                if (getConnectedMarbles(column, row) is Ok) return true
            }
        }

        return false
    }

    override fun getConnectedMarbles(
        column: Int,
        row: Int
    ): Result<List<Pair<Int, Int>>, FieldException> {
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
        return if (marble == null) Err(InvalidCoordinateException(column, row))
        else {
            val list = mutableListOf<Pair<Int, Int>>()
            getConnectedMarblesInternal(marble.color, column, row, list)
            if (list.size < minConnected) Err(NotEnoughConnectedMarblesException())
            else Ok(list)
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