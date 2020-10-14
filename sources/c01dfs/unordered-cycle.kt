package c01dfs

import utils.times

/**
 * Функция для поиска цикла в неориентированном графе
 *
 * @param graph: граф в виде списка ребер
 * @see [Graph]: Graph - то же самое, что и List<List<Int>>, или vector<vector<int>> в C++
 */
fun unorderedCycle(graph: Graph) {

    val n = graph.size
    val visited = false times n // массив из n значений false

    /**
     * Обход в глубину принимает текущую вершину и прошлую вершину и обходит всех соседей текущей
     *
     * @param v: текущая вершина
     * @param parent: прошлая вершина, передается, чтобы не находить цикл из одного ребра туда-обратно
     */
    fun dfs(v: Int, parent: Int) {
        visited[v] = true // отмечаем вершину посещенной

        for (u in graph[v]) { // перебираем всех соседей
            when {
                // если сосед еще не посещен, запускаем из него обход
                !visited[u] -> dfs(u, v)
                // если сосед посещен, но не является предыдущей вершиной, то мы нашли цикл
                u != parent -> TODO("Вывести цикл")
            }
        }
    }

    for (i in 0 until n) { // перебираем все вершины
        if (!visited[i]) {
            dfs(i, i) // от каждой еще не посещенной запускаем dfs
        }
    }

}