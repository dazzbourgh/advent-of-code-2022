package solution

import arrow.core.Either
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

    override fun solve1(input: Sequence<String>): Either<String, Number> =
        input.map { it.toCommand() }
            .flatMap { it.toSequence() }
            .let { sequenceOf(0) + it }
            .take(220)
            .runningFold(1) { acc, increment -> acc + increment }
            .mapIndexed { index, value -> index to (index * value) }
            .filter { (index, _) -> index == 20 || (index - 20) % 40 == 0 }
            .map { it.second }
            .also { println(it) }
            .sum()
            .right()

    override fun solve2(input: Sequence<String>): Either<String, Number> {
        return 1.right()
    }
}
