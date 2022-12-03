package solution

object Solution02 : Solution {
    override fun solve(input: Sequence<String>): Number {
        val scores = mapOf('X' to 1, 'Y' to 2, 'Z' to 3)
        val winningOutcomes = setOf('A' to 'Y', 'B' to 'Z', 'C' to 'X')
        val moves = input
            .map { it.split(' ') }
            .map { (first, second) -> first.first() to second.first() }
        return moves
            .map { (first, second) ->
                scores[second]!! + when {
                    (first to second) in winningOutcomes -> 6
                    first + 23 == second -> 3
                    else -> 0
                }
            }
            .sum()
    }
}
