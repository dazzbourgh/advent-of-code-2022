package solution

import arrow.core.Either

interface Solution {
    fun solve1(input: Sequence<String>): Either<String, Number>

    fun solve2(input: Sequence<String>): Either<String, Number>
}
