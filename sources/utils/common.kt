package utils

inline infix fun <reified T> T.times(n: Int) = MutableList(n) { this }

inline infix fun <reified T> T.times(nm: Pair<Int, Int>) =
        MutableList(nm.first) {
            MutableList(nm.second) { this }
        }

fun max(x: Long, y: Long?): Long {
    return y?.let { maxOf(x, y) } ?: x
}