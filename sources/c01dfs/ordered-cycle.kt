@file:Suppress("NON_EXHAUSTIVE_WHEN")

package c01dfs

import utils.times

/**
 * Функция для поиска цикла в ориентированном графе
 *
 * @param graph: граф в виде списка смежности
 * @see [Graph]: то же самое, что и List<List<Int>>, или vector<vector<int>> в C++
 * @see [Color]: цвета вершин (белый, серый, черный)
 */
fun orderedCycle(graph: Graph) {

    val n = graph.size
    val visited = Color.White times n // массив из n значений Color.White

    /**
     * Обход в глубину принимает текущую вершину и обходит ее соседей
     *
     * @param v: текущая вершина
     */
    fun dfs(v: Int) {
        visited[v] = Color.Grey // отмечаем, что вошли в вершину

        for (u in graph[v]) { // перебираем всех соседей
            when (visited[u]) {
                // если сосед еще не посещен, запускаем из него обход
                Color.White -> dfs(u)
                // если сосед на пути от корня до текущей, то мы нашли цикл
                Color.Grey -> TODO("Вывести цикл")
            }
        }

        visited[v] = Color.Black // отмечаем, что вышли из вершины
    }

    for (i in 0 until n) { // перебираем все вершины
        if (visited[i] == Color.White) {
            dfs(i) // от каждой еще не посещенной запускаем dfs
        }
    }

}