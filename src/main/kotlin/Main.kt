import arrow.core.getOrElse
import kotlinx.coroutines.runBlocking
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
                        lines?.let { solution.solve(it) }
                    }
            }
        }
        .zip(taskNums.asSequence())
        .map { (answer, index) -> answer?.let { index to it } }
        .filterNotNull()
        .forEach { (index, answer) ->
            println("Solution ${index.padStart(2, '0')}: $answer")
        }
}
