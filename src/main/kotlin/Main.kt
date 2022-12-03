import arrow.core.getOrElse
import kotlinx.coroutines.runBlocking
import util.fanOut
import util.instantiate
import util.readFile

fun main() {
    val taskNums = (1..30).map { it.toString().padStart(2, '0') }

    val solutions = taskNums.map { instantiate(it) }
        .filter { it.isRight() }
        .map { it.getOrElse { throw RuntimeException() } }

    taskNums
        .asSequence()
        .map { "$it.txt" }
        .map { readFile(it) }
        .zip(solutions.asSequence())
        .map { (res, solution) ->
            runBlocking {
                res.map { it?.lineSequence() }
                    .use { lines ->
                        lines?.fanOut()
                            ?.let { (s1, s2) ->
                                solution.solve1(s1) to solution.solve2(s2)
                            }
                    }
            }
        }
        .zip(taskNums.asSequence())
        .map { (answer, index) -> answer?.let { index to it } }
        .filterNotNull()
        .forEach { (index, answers) ->
            println(
                """
                |Solutions ${index.padStart(2, '0')}: ${answers.first}
                |              ${answers.second}
            """.trimMargin()
            )
        }
}
