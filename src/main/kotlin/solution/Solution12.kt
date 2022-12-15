package solution

import arrow.core.Either
import arrow.core.right
import util.Matrix
import util.combinations
import java.util.LinkedList
import java.util.Queue

object Solution12 : Solution {
    private fun parse(input: Sequence<String>): Matrix<Int> =
        input.map { it.split("") }
            .map { str -> str.filter { it.isNotBlank() }.map { c -> c.first().code - 'A'.code } }
            .toList()

    private fun dijkstra(matrix: Matrix<Int>, start: Pair<Int, Int>, end: Pair<Int, Int>): Int? {
        data class Entry(val coord: Pair<Int, Int>, val steps: Int)

        val paths = MutableList(matrix.size) { MutableList(matrix[0].size) { Int.MAX_VALUE } }
        paths[start.first][start.second] = 0
        val queue: Queue<Entry> = LinkedList(listOf(Entry(start, 0)))
        fun getValidNeighbors(coord: Pair<Int, Int>, steps: Int): List<Pair<Int, Int>> =
            listOf(0 to 1, 1 to 0, -1 to 0, 0 to -1)
                .map { (x, y) -> (coord.first + x) to (coord.second + y) }
                .filter { (x, y) ->
                    x in matrix.indices
                            && y in matrix[0].indices
                            && matrix[x][y] - matrix[coord.first][coord.second] < 2
                            && (paths[x][y] == Int.MAX_VALUE || paths[x][y] > steps)
                }
        while (queue.isNotEmpty() && queue.first().coord != end) {
            val (coord, steps) = queue.remove()
            val validNeighbors = getValidNeighbors(coord, steps + 1)
            validNeighbors.forEach { (x, y) ->
                paths[x][y] = steps + 1
                queue.add(Entry(x to y, steps + 1))
            }
        }
        return queue.firstOrNull()?.steps
    }

    private fun Matrix<Int>.findChars(vararg chars: Char) = indices.map { i -> List(this[0].size) { i } }
        .flatMap { it.zip(this[0].indices) }
        .filter { (x, y) ->
            val res = chars.any { this[x][y] == it.code - 'A'.code }
            res
        }

    override fun solve1(input: Sequence<String>): Either<String, Number> {
        val matrix = parse(input)
        val (start, end) = matrix.findChars('S', 'E')
            .map { (x, y) -> matrix[x][y] to (x to y) }
            .sortedByDescending { it.first }
            .map { it.second }
        val clean = matrix.map { row ->
            row.map {
                when (it) {
                    'S'.code - 'A'.code -> 'a'.code - 'A'.code
                    'E'.code - 'A'.code -> 'z'.code - 'A'.code
                    else -> it
                }
            }
        }
        return dijkstra(clean, start, end)!!.right()
    }

    override fun solve2(input: Sequence<String>): Either<String, Number> {
        val matrix = parse(input)
        val starts = matrix.findChars('a', 'S')
        val ends = matrix.findChars('E', 'z')
        return starts.combinations(ends)
            .mapNotNull { (start, end) -> dijkstra(matrix, start, end) }
            .min()
            .right()
    }
}
