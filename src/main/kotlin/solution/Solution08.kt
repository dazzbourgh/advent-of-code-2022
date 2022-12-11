package solution

import arrow.core.Either
import arrow.core.flatten
import arrow.core.right
import kotlin.math.max
import kotlin.math.min

typealias Matrix<T> = List<List<T>>

object Solution08 : Solution {
    private operator fun Matrix<Boolean>.plus(other: Matrix<Boolean>): Matrix<Boolean> =
        this.zip(other).map { (a, b) -> a.zip(b).map { (e1, e2) -> e1 || e2 } }

    private operator fun Matrix<Int>.times(other: Matrix<Int>): Matrix<Int> =
        this.zip(other).map { (a, b) -> a.zip(b).map { (e1, e2) -> e1 * e2 } }

    private fun <T> rotate(matrix: Matrix<T>): Matrix<T> =
        generateSequence<List<T>> { emptyList() }
            .take(matrix.size)
            .toList()
            .let { zero ->
                matrix.fold(zero) { acc, ts -> acc.zip(ts).map { (a, b) -> a + b } }
                    .map { it.reversed() }
            }

    private fun markVisible(a: Matrix<Int>): Matrix<Boolean> =
        a.map { row ->
            row.drop(1)
                .fold(row.first() to listOf(true)) { (m, ans), cur ->
                    max(m, cur) to (ans + (cur > m))
                }
                .second
        }

    private fun calculateVisibility(row: List<Int>): List<Int> {
        // if this looks like sorcery, google "monotonic stack"
        val stack = ArrayDeque<Pair<Int, Int>>()
        val ans = MutableList(row.size) { 0 }
        (row + 10).forEachIndexed { i, tree ->
            while (stack.isNotEmpty() && stack.last().first <= tree) {
                val (_, j) = stack.removeLast()
                ans[j] = min(i, row.size - 1) - j
            }
            stack.add(tree to i)
        }
        return ans
    }

    private fun <R> solve(
        input: Sequence<String>,
        f: (Matrix<Int>) -> Matrix<R>,
        reducer: (Matrix<R>, Matrix<R>) -> Matrix<R>
    ) =
        input.map { it.map { char -> char.digitToInt() } }
            .toList()
            .let { matrix -> generateSequence(matrix) { rotate(it) } }
            .take(4)
            .map(f)
            .mapIndexed { index, m ->
                generateSequence(m) { rotate(it) }
                    .drop(4 - index)
                    .first()
            }
            .reduce(reducer)
            .flatten()

    override fun solve1(input: Sequence<String>): Either<String, Number> =
        solve(input, Solution08::markVisible) { acc, m -> acc + m }
            .count { it }
            .right()

    override fun solve2(input: Sequence<String>): Either<String, Number> {
        val mapper = { matrix: Matrix<Int> -> matrix.map { calculateVisibility(it) } }
        return solve(input, mapper) { a, b -> a * b }
            .max()
            .right()
    }
}
