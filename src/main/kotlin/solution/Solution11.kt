package solution

import arrow.core.Either
import arrow.core.right
import arrow.core.tail
import util.product

typealias Worry = Long
typealias MonkeyIndex = Int

object Solution11 : Solution {
    private class Monkey(val worries: List<Worry>, val operation: Operation, val test: Test, val business: Long)
    private class Operation(val op: (Long) -> Long)
    private data class Test(val divisibleBy: Int, val success: TestResult, val failure: TestResult)
    private data class TestResult(val targetMonkey: Int)

    private val worryRegex = Regex(""".*Starting items: (?<worries>.*)""")
    private val operationRegex = Regex(""".*= old ([+*]) (\d+|old)""")
    private val testRegex = Regex(""".*divisible by (\d+)""")
    private val testResultRegex = Regex(""".*to monkey (\d+)""")

    private fun div(n: Long) = n / 3
    private fun mod(n: Long) = n % (3 * 5 * 2 * 11 * 13 * 17 * 7 * 19)

    private enum class TaskNum {
        ONE, TWO
    }

    private fun String.toTestResult() =
        testResultRegex.matchEntire(this)!!
            .groups[1]!!
            .value
            .toInt()
            .let { TestResult(it) }

    private fun String.toMonkey(taskNum: TaskNum): Monkey {
        val f: (Long) -> Long = when (taskNum) {
            TaskNum.ONE -> Solution11::div
            TaskNum.TWO -> Solution11::mod
        }
        return split("\n")
            .filterNot { it.isBlank() }
            .drop(1)
            .let { lines ->
                val (l1, l2, l3, l4, l5) = lines
                val worries = worryRegex.matchEntire(l1)!!
                    .groups["worries"]!!
                    .value
                    .split(", ")
                    .filterNot { it.isBlank() }
                    .map { it.toLong() }
                val operation =
                    operationRegex.matchEntire(l2)!!.groups.let<MatchGroupCollection, (Long) -> Long> { mgc ->
                        val op = mgc[1]!!.value
                        val value = mgc[2]!!.value
                        when {
                            op == "*" && value == "old" -> ({ it * it })
                            op == "+" && value == "old" -> ({ it + it })
                            op == "*" -> ({ it * value.toInt() })
                            else -> ({ it + value.toInt() })
                        }
                    }.let { g -> Operation { f(g(it)) } }
                val success = l4.toTestResult()
                val failure = l5.toTestResult()
                val test = Test(testRegex.matchEntire(l3)!!.groups[1]!!.value.toInt(), success, failure)
                Monkey(worries, operation, test, 0)
            }
    }

    private fun parseMonkeys(input: Sequence<String>, taskNum: TaskNum): List<Monkey> =
        input.fold(listOf("")) { acc, s ->
            if (s.isBlank()) acc + ""
            else acc.subList(0, acc.size - 1) + (acc.last() + "\n" + s)
        }
            .dropLast(1)
            .map { it.toMonkey(taskNum) }

    private fun Test.runTest(worry: Worry): TestResult =
        if (worry % divisibleBy == 0L) success else failure

    private fun process(monkey: Monkey): Triple<Monkey, Worry, MonkeyIndex> {
        val worry = monkey.worries.first()
        val updatedWorry = monkey.operation.op(worry)
        val testResult = monkey.test.runTest(updatedWorry)
        return Triple(
            Monkey(monkey.worries.tail(), monkey.operation, monkey.test, monkey.business + 1),
            updatedWorry,
            testResult.targetMonkey
        )
    }

    private fun runRound(monkeys: List<Monkey>): List<Monkey> {
        val mutableMonkeys = monkeys.toMutableList()
        for (i in monkeys.indices) {
            while (mutableMonkeys[i].worries.isNotEmpty()) {
                val monkey = mutableMonkeys[i]
                val (updatedMonkey, worry, targetMonkey) = process(monkey)
                mutableMonkeys[i] = updatedMonkey
                val m = mutableMonkeys[targetMonkey]
                mutableMonkeys[targetMonkey] = Monkey(m.worries + worry, m.operation, m.test, m.business)
            }
        }
        return mutableMonkeys.toList()
    }

    private fun solve(input: Sequence<String>, rounds: Int, taskNum: TaskNum): Either<String, Number> {
        val monkeys = parseMonkeys(input, taskNum)
        return generateSequence(monkeys) { runRound(it) }
            .drop(rounds)
            .first()
            .map { it.business }
            .sortedDescending()
            .take(2)
            .product()
            .right()
    }

    override fun solve1(input: Sequence<String>): Either<String, Number> =
        solve(input, 20, TaskNum.ONE)

    override fun solve2(input: Sequence<String>): Either<String, Number> =
        solve(input, 10_000, TaskNum.TWO)
}
