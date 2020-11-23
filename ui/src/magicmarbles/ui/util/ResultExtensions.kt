package magicmarbles.ui.util

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result

fun <T, E : Exception> Result<T, E>.throwOnFailure() {
    when (this) {
        is Ok -> Unit
        is Err -> throw error
    }
}