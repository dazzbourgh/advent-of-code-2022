package solution

import arrow.core.Either
import arrow.core.right
import arrow.core.tail
import util.product
import java.util.*

object Solution13 : Solution {
    private sealed class Packet
    private data class Num(val n: Int) : Packet()
    private data class Arr(val packets: List<Packet>) : Packet()

    private fun tokenize(s: String): List<String> {
        val ans = mutableListOf<String>()
        val token = LinkedList<Char>()
        val tokens = LinkedList<String>()
        var braces = 0
        for (c in s) {
            token.add(c)
            when (c) {
                ',' -> {
                    if (braces == 0) {
                        token.removeLast()
                        tokens.add(token.joinToString(""))
                        token.clear()
                    } else {
                        token.add(c)
                    }
                }

                '[' -> braces += 1
                ']' -> {
                    braces -= 1
                    if (braces == 0) {
                        tokens.add(token.joinToString(""))
                        token.clear()
                    }
                }
            }
        }
        if (token.isNotEmpty()) tokens.add(token.joinToString(""))
        for (t in tokens) if (t.isNotBlank()) ans.add(t)
        return ans
    }

    private fun parse(s: String): Packet =
        when {
            s.startsWith("[") -> Arr(tokenize(s.substring(1 until s.length - 1)).map { parse(it) })
            else -> Num(s.toInt())
        }

    private fun parse(input: Sequence<String>): List<Pair<Packet, Packet>> =
        input.fold(listOf<List<String>>(listOf())) { acc, line ->
            if (line.isBlank()) acc + listOf(listOf())
            else acc.dropLast(1) + listOf(acc.last() + line)
        }
            .filter { it.isNotEmpty() }
            .map { (left, right) -> parse(left) to parse(right) }

    private fun compare(a: Packet, b: Packet): Int = when {
        a is Arr && b is Num -> compare(a, Arr(listOf(b)))
        a is Num && b is Arr -> compare(Arr(listOf(a)), b)
        a is Num && b is Num -> a.n - b.n
        a is Arr && b is Arr -> {
            when {
                a.packets.isEmpty() && b.packets.isNotEmpty() -> -1
                a.packets.isNotEmpty() && b.packets.isEmpty() -> 1
                a.packets.isEmpty() -> 0
                else -> {
                    val res = compare(a.packets.first(), b.packets.first())
                    if (res == 0) compare(Arr(a.packets.tail()), Arr(b.packets.tail())) else res
                }
            }
        }

        else -> throw RuntimeException("really, kotlin?")
    }

    override fun solve1(input: Sequence<String>): Either<String, Number> =
        parse(input)
            .mapIndexed { i, (left, right) -> (i + 1) to compare(left, right) }
            .filter { (_, res) -> res <= 0 }
            .sumOf { it.first }
            .right()

    override fun solve2(input: Sequence<String>): Either<String, Number> {
        val two = Arr(listOf(Num(2)))
        val six = Arr(listOf(Num(6)))
        return parse(input)
            .flatMap { (a, b) -> listOf(a, b) }
            .let { it + two + six }
            .sortedWith { o1, o2 -> compare(o1, o2) }
            .let { it.indices.zip(it) }
            .filter { (_, packet) -> packet == two || packet == six }
            .map { it.first + 1 }
            .product()
            .right()
    }
}
