package utils

inline infix fun <reified T> T.times(n: Int) = MutableList(n) { this }

fun max(x: Long, y: Long?): Long {
    return y?.let { maxOf(x, y) } ?: x
}