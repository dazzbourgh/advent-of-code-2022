package solution

object Solution01 : Solution {
    private fun calculateSums(input: Sequence<String>) =
        input.fold(listOf(listOf<String>())) { acc, string ->
            if (string.isNotBlank()) acc.take(acc.size - 1) + listOf(acc.last() + listOf(string))
            else acc + listOf(listOf())
        }
            .map { vals -> vals.sumOf { it.toInt() } }

    override fun solve1(input: Sequence<String>): Number =
        calculateSums(input).max()

    override fun solve2(input: Sequence<String>): Number =
        calculateSums(input).sortedDescending().take(3).sum()
}
