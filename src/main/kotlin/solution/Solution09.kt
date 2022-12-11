package solution

import arrow.core.Either
import arrow.core.flatten
import arrow.core.right
import util.power
import kotlin.math.abs
import kotlin.math.sign
import kotlin.math.sqrt

object Solution09 : Solution {
    enum class Direction { U, D, L, R }
    data class Command(val direction: Direction, val moves: Int)
    data class Coord(val x: Int, val y: Int)
    data class State(val h: Coord, val t: Coord)

    private fun String.toCommand() = split(" ").let { (cmd, moves) -> Command(Direction.valueOf(cmd), moves.toInt()) }
    private fun moveH(h: Coord, direction: Direction): Coord = when (direction) {
        Direction.U -> Coord(h.x, h.y + 1)
        Direction.D -> Coord(h.x, h.y - 1)
        Direction.L -> Coord(h.x - 1, h.y)
        Direction.R -> Coord(h.x + 1, h.y)
    }

    private fun moveT(t: Coord, h: Coord): Coord = when {
        sqrt((h.x - t.x).power(2) + (h.y - t.y).power(2)) > sqrt(2.0) -> {
            val x = sign(h.x.toFloat() - t.x).toInt()
            val y = sign(h.y.toFloat() - t.y).toInt()
            Coord(t.x + x, t.y + y)
        }

        abs(h.x - t.x) > 1 -> Coord(t.x + sign(h.x.toFloat() - t.x).toInt(), t.y)
        abs(h.y - t.y) > 1 -> Coord(t.x, t.y + sign(h.y.toFloat() - t.y).toInt())
        else -> t
    }

    private fun move(state: State, direction: Direction): State {
        val (h, t) = state
        val nextH = moveH(h, direction)
        val nextT = moveT(t, nextH)
        return State(nextH, nextT)
    }

    override fun solve1(input: Sequence<String>): Either<String, Number> {
        val start = State(Coord(0, 0), Coord(0, 0))
        return input.map { it.toCommand() }
            .runningFold(sequenceOf(start)) { acc, (direction, moves) ->
                generateSequence(acc.last()) { move(it, direction) }
                    .take(moves + 1)
            }
            .flatten()
            .map { it.t }
            .distinct()
            .count()
            .right()
    }

    override fun solve2(input: Sequence<String>): Either<String, Number> =
        1.right()
}
