package c07dp

import utils.times

/**
 * Все потенциальные изменения, которые могут быть применены к строке.
 *
 * В этом коде он добавлен только для понимания происходящего,
 * в олимпиадной задаче можно "закодировать" переходы числами 0..3
 */
enum class Edit {
    /*
    dp[i][j] <- dp[i][j - 1] + 1
     - в начале из s1[0..(i - 1)] получаем s2[0..(j - 2)]
     - затем вставляем в конец s2[j - 1]
     */
    Insert,

    /*
    dp[i][j] <- dp[i - 1][j] + 1
     - в начале из s1[0..(i - 2)] получаем s2[0..(j - 1)]
     - удаляем стоящий дальше s1[i - 1]
     */
    Delete,

    /*
    dp[i][j] <- dp[i - 1][j - 1] + 1
     - в начале из s1[0..(i - 2)] получаем s2[0..(j - 2)]
     - затем заменяем s1[i - 1] на s2[j - 1]
     */
    Change,

    /*
    dp[i][j] <- dp[i - 1][j - 1]
     - если s1[i - 1] = s2[j - 1], то можно ничего не делать
     */
    Skip
}

fun findEditDistance(s1: String, s2: String) {

    /*
    Массив размера (s1.length+1) * (s2.length+1), заполненный нулями
     - dp[i][j] хранит редакционное расстояние
       между s1[0..(i - 1)] и s2[0..(j - 1)]
     */
    val dp = 0 times Pair(s1.length + 1, s2.length + 1)
    /*
    Массив размера (s1.length+1) * (s2.length+1), заполненный Skip
     - parent[i][j] хранит последнее действие в цепочке преобразований
       между s1[0..(i - 1)] и s2[0..(j - 1)]
     */
    val last = Edit.Skip times Pair(s1.length + 1, s2.length + 1)

    /**
     * Функция [relax] пробует обновить значение динамики в (i, j),
     * используя действие [edit]
     */
    fun relax(i: Int, j: Int, edit: Edit) {
        val newDistance = when (edit) {
            Edit.Insert -> dp[i][j - 1] + 1
            Edit.Delete -> dp[i - 1][j] + 1
            Edit.Change -> dp[i - 1][j - 1] + 1
            Edit.Skip -> dp[i - 1][j - 1]
        }
        // если такое изменение оптимально
        if (newDistance < dp[i][j]) {
            // обновляем расстояние
            dp[i][j] = newDistance
            // обновляем последнее действие
            last[i][j] = edit
        }
    }

    /*
    Определяем базу динамики: dp[0][j] и dp[i][0]
     */
    for (j in 1..s2.length) {
        dp[0][j] = j
        last[0][j] = Edit.Insert
    }
    for (i in 1..s1.length) {
        dp[i][0] = i
        last[i][0] = Edit.Delete
    }

    /*
    Определяем переходы динамики: перебор по [Edit]
     */
    for (i in 1..s1.length) {
        for (j in 1..s2.length) {
            // у нас всегда есть опция сделать замену
            dp[i][j] = dp[i - 1][j - 1] + 1
            last[i][j] = Edit.Change

            // еще мы можем сделать insert или delete
            relax(i, j, Edit.Insert)
            relax(i, j, Edit.Delete)

            // если последние символы равны, то можно не делать лишнее действие
            if (s1[i - 1] == s2[j - 1]) {
                relax(i, j, Edit.Skip)
            }
        }
    }

    // выводим ответ
    println(dp[s1.length][s2.length])

    /*
    Восстанавливаем последовательность действий
     - последнее сделанное действие - last[s1.length][s2.length]
     - запомним его и "откатим" его назад
     - повторим, пока не вернемся к пустым суффиксам
     */
    var i = s1.length
    var j = s2.length
    // пустой список действий
    val actions = mutableListOf<String>()
    while (i > 0 || j > 0) {
        when (last[i][j]) {
            Edit.Insert -> {
                j--
                actions.add("inserted '${s2[j]}' on position $j")
            }
            Edit.Delete -> {
                i--
                actions.add("deleted '${s1[i]}' from position $j")
            }
            Edit.Change -> {
                i--; j--
                actions.add("changed '${s1[i]}' to '${s2[j]}' on position $j")
            }
            Edit.Skip -> {
                i--; j--
            }
        }
    }

    // разворачиваем действия в нужном порядке и выводим
    actions.reversed().forEach { println(it) }

}

fun main() {

    println("---")
    findEditDistance(
            "abacabz",
            "abracadabra"
    )
    /*
    5
    inserted 'r' on position 2
    inserted 'd' on position 6
    inserted 'a' on position 7
    inserted 'r' on position 9
    changed 'z' to 'a' on position 10
     */

    println("---")
    findEditDistance(
            "балалайка",
            "бакалея"
    )
    /*
    5
    changed 'л' to 'к' on position 2
    deleted 'а' from position 5
    deleted 'й' from position 5
    changed 'к' to 'е' on position 5
    changed 'а' to 'я' on position 6
     */

}