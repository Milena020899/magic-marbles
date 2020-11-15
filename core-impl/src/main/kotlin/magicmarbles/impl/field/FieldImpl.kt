package magicmarbles.impl.field

import magicmarbles.api.field.*
import magicmarbles.impl.util.countRemovedBy
import magicmarbles.impl.util.prepend

class FieldImpl private constructor(
    private var marbleField: MutableList<MutableList<Marble?>>, override val width: Int, override val height: Int,
) : Field, ModifiableField, MoveableField {

    override val field: List<List<Marble?>>
        get() = marbleField

    override fun move(column: Int, row: Int): Int? {
        if (!checkBounds(column, row)) return null
        val marblesToRemove = getConnectedMarbles(column, row)
        if (marblesToRemove == null || marblesToRemove.size == 1) return null

        marblesToRemove.forEach { (_r, _c) -> this[_r, _c] = null }

        val removedMarbles = marbleField.map {
            val removedMarbles = it.countRemovedBy { marble -> marble == null }
            it.prepend(removedMarbles) { null }
            removedMarbles
        }.sum()

        val removedColumns = marbleField.countRemovedBy { it.all { marble -> marble == null } }
        marbleField.prepend(removedColumns) { MutableList(height) { null } }

        return removedMarbles
    }

    override fun movesPossible(connectedCount: Int): Boolean {
        marbleField.indices.forEach { column ->
            marbleField[column].indices.forEach { row ->
                getConnectedMarbles(column, row)?.size?.let {
                    if (it >= connectedCount) return true
                }
            }
        }

        return false
    }

    override fun isEmpty() = marbleField.isEmpty()

    override fun marbleCount(): Int {
        return marbleField
            .map { it.count { marble -> marble != null } }
            .sum()
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
            list
        }
    }

    override operator fun get(column: Int, row: Int): Marble? =
        if (!checkBounds(column, row)) null else marbleField[column][row]

    override operator fun set(column: Int, row: Int, marble: Marble?) {
        if (!checkBounds(column, row)) return
        marbleField[column][row] = marble
    }

    override fun map(transformer: (Int, Int) -> Marble?) {
        marbleField.forEachIndexed { columnIndex, column ->
            column.indices
                .forEach { rowIndex -> this[columnIndex, rowIndex] = transformer(columnIndex, columnIndex) }
        }
    }

    private fun checkBounds(column: Int, row: Int) = column in 0 until width && row in 0 until height

    companion object Factory : ModifiableFieldFactory {
        override fun createEmptyField(width: Int, height: Int): ModifiableField =
            FieldImpl(
                MutableList(width) { MutableList(height) { null } },
                width, height
            )
    }
}