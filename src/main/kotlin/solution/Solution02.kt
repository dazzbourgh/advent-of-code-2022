package solution

object Solution02 : Solution {
    private val scores = mapOf('X' to 1, 'Y' to 2, 'Z' to 3)
    private val winningOutcomes = mapOf('A' to 'Y', 'B' to 'Z', 'C' to 'X')
    private val losinggOutcomes = mapOf('A' to 'Z', 'B' to 'X', 'C' to 'Y')

    private fun solve(input: Sequence<String>, solution: (Pair<Char, Char>) -> Int): Int =
        input
            .map { it.split(' ') }
            .map { (first, second) -> first.first() to second.first() }
            .map(solution)
            .sum()

    override fun solve1(input: Sequence<String>): Number =
        solve(input) { (first, second) ->
            scores[second]!! + when (second) {
                winningOutcomes[first] -> 6
                first + 23 -> 3
                else -> 0
            }
        }

    override fun solve2(input: Sequence<String>): Number =
        solve(input) { (enemyMove, outcome) ->
            val score = (outcome - 'X') * 3
            val move = when (score) {
                6 -> winningOutcomes[enemyMove]?.let { scores[it] }!!
                3 -> scores[enemyMove + ('X' - 'A')]!!
                else -> losinggOutcomes[enemyMove]?.let { scores[it] }!!
            }
            score + move
        }
}
