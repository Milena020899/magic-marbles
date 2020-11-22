package magicmarbles.api.field

import com.github.michaelbull.result.Result

interface PlayableField : Field {
    fun move(column: Int, row: Int): Result<Int, FieldException>
}
