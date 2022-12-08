package solution

import arrow.core.Either
import arrow.core.right

object Solution04 : Solution {
    private data class Interval(val left: Int, val right: Int)

    private fun parse(s: String): Pair<Interval, Interval> {
        val (first, second) = s.split(",")
        fun String.toInterval() = split("-").let { (l, r) -> Interval(l.toInt(), r.toInt()) }
        return first.toInterval() to second.toInterval()
    }

    private infix fun Interval.contains(other: Interval): Boolean =
        left <= other.left && right >= other.right

    private infix fun Interval.overlapsWith(other: Interval): Boolean =
        other.left in left..right || other.right in left..right

    private fun solve(input: Sequence<String>, predicate: (Pair<Interval, Interval>) -> Boolean): Int =
        input.map { parse(it) }.count(predicate)

    override fun solve1(input: Sequence<String>): Either<String, Number> =
        solve(input) { (first, second) ->
            first contains second || second contains first
        }
            .right()

    override fun solve2(input: Sequence<String>): Either<String, Number> =
        solve(input) { (first, second) ->
            first overlapsWith second || second overlapsWith first
        }
            .right()
}
