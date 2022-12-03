package solution

object Solution03 : Solution {
    private fun findDuplicate(s1: String, s2: String): String =
        s2.toSet().let { set2 -> s1.filter { set2.contains(it) } }

    private val values = (('a'..'z') + ('A'..'Z')).zip(1..52).toMap()

    private fun getValue(c: Char): Int = values[c]!!

    private fun String.splitInHalf(): Pair<String, String> {
        val (first, second) = windowed(length / 2, length / 2)
        return first to second
    }

    private fun Sequence<String>.finalize(): Int =
        map { it.first() }
            .map { getValue(it) }
            .sum()

    override fun solve1(input: Sequence<String>): Number =
        input.map { it.splitInHalf() }
            .map { (a, b) -> findDuplicate(a, b) }
            .finalize()

    override fun solve2(input: Sequence<String>): Number =
        input.windowed(3, 3)
            .map { it.reduce(Solution03::findDuplicate) }
            .finalize()
}
