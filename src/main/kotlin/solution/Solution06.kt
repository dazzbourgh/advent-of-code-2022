package solution

import arrow.core.Either
import arrow.core.left
import arrow.core.right

object Solution06 : Solution {
    private fun solve(input: Sequence<String>, windowSize: Int): Either<String, Number> =
        input.first()
            .asSequence()
            .windowed(windowSize)
            .zip(generateSequence(0) { it + 1 })
            .filter { (chars, _ ) -> chars.toSet().size == windowSize }
            .map { it.second }
            .first()
            .let { it + windowSize }
            .right()

    override fun solve1(input: Sequence<String>): Either<String, Number> =
        solve(input, 4)

    override fun solve2(input: Sequence<String>): Either<String, Number> =
        solve(input, 14)
}
