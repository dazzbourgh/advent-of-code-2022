package solution

import arrow.core.Either
import arrow.core.left
import arrow.core.right

object Solution10 : Solution {
    private sealed class Command
    private object Noop : Command()
    private data class Addx(val value: Int) : Command()

    private fun String.toCommand() = if (startsWith("n")) Noop else Addx(split(" ")[1].toInt())
    private fun Command.toSequence() = when (this) {
        is Addx -> sequenceOf(0, value)
        Noop -> sequenceOf(0)
    }

    private fun steps(input: Sequence<String>) =
        input.map { it.toCommand() }
            .flatMap { it.toSequence() }
            .let { sequenceOf(0) + it }
            .take(240)
            .runningFold(1) { acc, increment -> acc + increment }

    override fun solve1(input: Sequence<String>): Either<String, Number> =
        steps(input)
            .mapIndexed { index, value -> index to (index * value) }
            .filter { (index, _) -> index == 20 || (index - 20) % 40 == 0 }
            .map { it.second }
            .also { println(it) }
            .sum()
            .right()

    private fun List<Int>.toLine() =
        mapIndexed { index, sprite -> if (index in (sprite - 1..sprite + 1)) ':' else ' ' }
            .joinToString("")

    override fun solve2(input: Sequence<String>): Either<String, Number> =
        steps(input).drop(1)
            .chunked(40)
            .map { it.toLine() }
            .joinToString("\n", prefix = "\n")
            .left()
}
