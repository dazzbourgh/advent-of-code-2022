package solution

object Solution01 : Solution {
    override fun solve(input: Sequence<String>): Number =
        input.fold(listOf(listOf<String>())) { acc, string ->
            if (string.isNotBlank()) acc.take(acc.size - 1) + listOf(acc.last() + listOf(string))
            else acc + listOf(listOf())
        }
            .maxOfOrNull { vals -> vals.sumOf { it.toInt() } } ?: -1
}
