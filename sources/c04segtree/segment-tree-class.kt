@file:Suppress("NON_EXHAUSTIVE_WHEN")

package c04segtree

import utils.max
import kotlin.test.fail

/**
 * В этом файле находится реализация дерева отрезков на структурах\классах,
 * дети каждой вершины хранятся в самой вершине
 */

/**
 * Класс [Node] отвечает за отрезок дерева отрезков на индексах с [l] по [r]
 * В данной реализации левая граница включается в отрезок, а правая - нет, поэтому
 * соответствующий отрезок - это полуинтервал [l, r)
 *
 * Данная реализация будет решать задачу RSMQ - находить максимум и сумму на отрезке,
 * и устанавливать значения на отрезке равными заданному или увеличивать значения на
 * отрезке на заданное число
 *
 * @param l: левая граница отрезка (включительно)
 * @param r: правая граница отрезка (исключительно)
 */
sealed class Node(val l: Int, val r: Int) {

    /**
     * Поля вершины, хранящие, соответственно, максимум и сумму на отрезке с [l] по [r]
     * без учета примененных обновлений в этой вершине
     */
    protected var maximum: Long = 0 // максимум без учета обновлений
    protected var sum: Long = 0     // сумма без учета обновлений

    /**
     * Поля вершины, хранящие последнее актуальное изменение, произошедшее в этой вершине
     * Единственность будет гарантироваться тем, что
     *  - вызов `set` после любой другой операции делает ее неактуальной
     *  - вызов `add` поверх другой операции просто увеличивает ее параметр
     *      - `add(x); add(y)` равносильно `add(x + y)`
     *      - `set(x); add(y)` равносильно `set(x + y)`
     *
     * @see UpdateType: None, Set или Add в зависимости от типа последнего изменения
     */
    protected var update: Long = 0
    protected var updateType: UpdateType = UpdateType.None

    /**
     * Функция [push] проталкивает операции обновления в детей
     *
     * Это требуется делать тогда, когда, например, операция `add` пришла на часть
     * отрезка, на который до этого сделали `set` - в таком случае важно не терять
     * порядок применения этих операций к детям
     *
     * Модификатор `abstract` означает, что в [Point] и [Segment] эта функция будет
     * работать по-разному, и ее поведение будет определено позже
     */
    protected abstract fun push()

    /**
     * Поле [length] возвращает длину отрезка
     * Поскольку [r] не входит в отрезок, то длина в точности равна r - l
     */
    val length get() = r - l

    /**
     * Функция [trueMaximum] возвращает максимум с учетом
     * примененных к отрезку операций обновления
     */
    protected fun trueMaximum() = when (updateType) {
        UpdateType.Set -> update           // если был `set(x)`, то x
        UpdateType.Add -> maximum + update // если был `add(x)`, то maximum + x
        else -> maximum
    }

    /**
     * Функция [trueSum] возвращает сумму с учетом
     * примененных к отрезку операция обновления
     */
    protected fun trueSum() = when (updateType) {
        UpdateType.Set -> update * length       // если был `set(x)`, то x * length
        UpdateType.Add -> sum + update * length // если был `add(x)`, то sum + x * length
        else -> sum
    }

    /**
     * Функция [set] отвечает за присвоение значения [x] всем элементам на отрезке
     */
    protected fun set(x: Long) {
        updateType = UpdateType.Set
        update = x
    }

    /**
     * Функция [add] отвечает за увеличение всех элементов на отрезке на [x]
     */
    protected fun add(x: Long) {
        update += x
        if (updateType == UpdateType.None) {
            updateType = UpdateType.Add
        }
    }

    /**
     * Класс [Point] отвечает за нижнюю вершину (лист) дерева отрезков,
     * которому соответствует элемент исходного массива с индексом [index] и значением [value]
     * В терминах полуинтервалов - это [index, index + 1)
     *
     * @param index: индекс в исходном массиве, соответствующий этому листу
     */
    class Point(index: Int, value: Long) : Node(index, index + 1) {

        /**
         * Значения в листе инициализируются тем значением, которое лежало в исходном массиве
         * Функция `init` вызывается сразу после вызова `Point(index, value)`
         */
        init {
            maximum = value
            sum = value
        }

        /**
         * Поскольку у листов дерева нет детей, то [push] вместо проталкивания вниз
         * просто явным образом применяет сохраненное изменение к хранящемуся в листе значению
         * и обнуляет [update]
         */
        override fun push() {
            when (updateType) {
                UpdateType.Set -> maximum = update
                UpdateType.Add -> maximum += update
            }
            sum = maximum
            updateType = UpdateType.None
            update = 0
        }

    }

    /**
     * Класс [Segment] отвечает за внутренние вершины дерева отрезков
     * Его поля [left] и [right] - это его левый и правый ребенок, соответственно
     *
     * Если его левый ребенок отвечает за отрезок [left.l, left.r), а правый за [right.l, right.r), то
     *  - мы требуем, чтобы left.r был равен right.l
     *  - а тогда отрезок в данной вершине - это [left.l, right.r)
     *
     * @param left: левый ребенок
     * @param right: правый ребенок
     * @constructor: ожидается, что левый и правый ребенок имеют стыкующиеся границы отрезков
     */
    class Segment internal constructor(val left: Node, val right: Node) : Node(left.l, right.r) {

        /**
         * Значения в вершине пересчитываются через значения в ее детях
         * Функция `init` вызывается сразу после вызова `Segment(left, right)`
         */
        init {
            relax()
        }

        /**
         * Функция [relax] служит для обновления значений в данном отрезке через детей
         * вершины, то есть через левую и правую половины отрезка
         */
        internal fun relax() {
            maximum = max(left.trueMaximum(), right.trueMaximum())
            sum = left.trueSum() + right.trueSum()
        }

        /**
         * Из внутренней вершины [push] проталкивает изменения в детей, после чего обновляет
         * значения максимума и суммы
         *
         * @see set: корректная операция присваивания в отрезке
         * @see add: корректная операция добавления в отрезке
         */
        override fun push() {
            when (updateType) {
                UpdateType.Set -> {
                    left.set(update)
                    right.set(update)
                }
                UpdateType.Add -> {
                    left.add(update)
                    right.add(update)
                }
            }
            updateType = UpdateType.None
            update = 0
            relax()
        }

    }

    /**
     * Эта конструкция позволяет делать методы, не принадлежащие конкретному объекту
     * В частности, на момент вызова функции [build] само дерево еще не построено,
     * но мы можем вызвать ее как `Node.build(values)`
     */
    companion object {

        /**
         * Функция [build] принимает исходный массив чисел, строит по нему
         * дерево отрезков и возвращает корень построенного дерева
         */
        fun build(values: List<Long>): Node {
            return build(0, values.size, values)
        }

        /**
         * Внутренняя реализация [build], строящая и возвращающая вершину дерева,
         * соответствующую отрезку с [l] по [r] в массиве [values]
         */
        private fun build(l: Int, r: Int, values: List<Long>): Node {
            return if (r - l > 1) {
                // если отрезок длины больше 1, делим его пополам,
                // рекурсивно строим детей и возвращаем вершину с двумя полученными детьми
                val middle = (l + r) / 2
                Segment(
                        build(l, middle, values),
                        build(middle, r, values)
                )
            } else {
                // иначе создаем лист, соответствующий элементу массива с индексом l
                Point(l, values[l])
            }
        }

    }

    /**
     * Далее следует основной публичный интерфейс класса [Node] - методы,
     * позволяющие получить интересующие нас значения на нужных отрезках или
     * применить требуемые изменения к заданным отрезкам
     */

    /**
     * Функция [getMaximum] возвращает максимальный элемент на отрезке с [from] до [to]
     * Обратите внимание, что [fail] в последнем случае никогда не достижим, потому что
     * не бывает так, что единственный элемент пересекается с [from, to), но не лежит в нем
     */
    fun getMaximum(from: Int, to: Int): Long = when {
        to <= l || from >= r -> Long.MIN_VALUE // если [from, to) не пересекается с текущим отрезком, максимума нет
        from <= l && to >= r -> trueMaximum()  // если [from, to) содержит текущий отрезок, то максимум на нем известен
        this is Segment ->                     // иначе берем максимум из максимумов в детях
            max(left.getMaximum(from, to), right.getMaximum(from, to))
        else -> fail()
    }

    /**
     * Функция [getSum] возвращает сумму элементов на отрезке с [from] до [to]
     * Аналогично, последняя ветка `when` недостижима
     */
    fun getSum(from: Int, to: Int): Long = when {
        to <= l || from >= r -> 0         // если [from, to) не пересекается с текущим отрезком, сумма 0
        from <= l && to >= r -> trueSum() // если [from, to) содержит текущий отрезок, то сумма на нем известна
        this is Segment ->                // иначе берем сумму сумм в детях
            left.getSum(from, to) + right.getSum(from, to)
        else -> fail()
    }

    /**
     * Функция [add] увеличивает все элементы на отрезке с [from] до [to] на [x]
     *
     * @see add: во второй ветке мы используем [add], реализующий прибавление в вершине
     */
    fun add(from: Int, to: Int, x: Long) {
        when {
            to <= l || from >= r -> return // если [from, to) не пересекается с текущим отрезком, не делаем ничего
            from <= l && to >= r -> add(x) // если [from, to) содержит текущий отрезок, то делаем локальный `add`
            this is Segment -> {           // иначе обновляем детей
                push()
                left.add(from, to, x)
                right.add(from, to, x)
                relax()
            }
        }
    }

    /**
     * Функция [set] присваивает всем элементам на отрезке с [from] до [to] значение [x]
     *
     * @see set: во второй ветке мы используем [set], реализующий присваивание в вершине
     */
    fun set(from: Int, to: Int, x: Long) {
        when {
            to <= l || from >= r -> return // если [from, to) не пересекается с текущим отрезком, не делаем ничего
            from <= l && to >= r -> set(x) // если [from, to) содержит текущий отрезок, то делаем локальный `set`
            this is Segment -> {           // иначе обновляем детей
                push()
                left.set(from, to, x)
                right.set(from, to, x)
                relax()
            }
        }
    }

}