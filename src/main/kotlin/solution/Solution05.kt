package solution

import arrow.core.Either
import arrow.core.left
import util.zipWithIndex

typealias Stack = Int

object Solution05 : Solution {
    private data class Move(val count: Int, val from: Stack, val to: Stack)
    private data class Board(val stacks: Map<Stack, MutableList<Char>>)

    private val regex = Regex("""move (\d+) from (\d+) to (\d+)""")
    private fun parseMove(s: String): Move {
        val groups = regex.matchEntire(s)!!.groups
        return (1..3).map { groups[it]!!.value.toInt() }
            .toList()
            .let { Move(it[0], it[1], it[2]) }
    }

    private fun parse(input: Sequence<String>): Pair<Board, Sequence<Move>> {
        val boardStrings = input.takeWhile { it.isNotBlank() }
        val board = boardStrings.takeWhile { '[' in it }
            .map { line ->
                line.zipWithIndex()
                    .filter { (_, c) -> c.isLetter() }
                    .toMap()
            }
            .fold(mutableMapOf<Stack, MutableList<Char>>()) { acc, m ->
                m.forEach { (stack, c) ->
                    val i = (stack - 1) / 4 + 1
                    if (i !in acc) acc[i] = mutableListOf()
                    acc[i]!!.add(c)
                }
                acc
            }
            .let { Board(it) }
        return board to input.dropWhile { it.isNotBlank() }.drop(1).map { parseMove(it) }
    }

    private fun solve(input: Sequence<String>, block: (List<Char>) -> List<Char>): Either<String, Number> {
        val (board, moves) = parse(input)
        moves.forEach { (count, from, to) ->
            val list = mutableListOf<Char>()
            repeat(count) {
                list.add(board.stacks[from]!!.removeFirst())
            }
            block(list).forEach { c ->
                board.stacks[to]!!.add(0, c)
            }
        }
        val ans = board.stacks
            .asSequence()
            .sortedBy { it.key }
            .map { (_, containers) -> containers.first() }
            .joinToString("")
        return ans.left()
    }

    override fun solve1(input: Sequence<String>): Either<String, Number> =
        solve(input) { it }

    override fun solve2(input: Sequence<String>): Either<String, Number> =
        solve(input) { it.reversed() }
}
