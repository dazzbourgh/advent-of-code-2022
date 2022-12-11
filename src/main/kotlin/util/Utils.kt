package util

import arrow.core.Either
import arrow.core.continuations.effect
import arrow.core.left
import arrow.core.right
import arrow.core.unzip
import arrow.fx.coroutines.Resource
import arrow.fx.coroutines.continuations.resource
import arrow.fx.coroutines.release
import kotlinx.coroutines.runBlocking
import solution.Solution
import java.io.BufferedReader
import kotlin.math.pow

fun readFile(path: String): Resource<BufferedReader?> =
    resource { javaClass.classLoader.getResourceAsStream(path)?.bufferedReader() } release { it?.close() }

fun instantiate(suffix: String): Either<Throwable, Solution> = runBlocking {
    effect<Throwable, Solution> {
        Class.forName("solution.Solution$suffix").kotlin.objectInstance as Solution
    }.fold(
        { it.left() },
        { it.left() },
        { it.right() }
    )
}

fun <T> Sequence<T>.fanOut(): Pair<Sequence<T>, Sequence<T>> {
    val list = toList()
    val (l1, l2) = list.zip(list).unzip()
    return l1.asSequence() to l2.asSequence()
}

fun String.zipWithIndex(): Iterable<Pair<Int, Char>> =
    mapIndexed { index, t -> index to t }

fun Number.power(n: Int) = toDouble().pow(n)
