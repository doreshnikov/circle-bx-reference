package utils

inline infix fun <reified T> T.times(n: Int) = MutableList(n) { this }