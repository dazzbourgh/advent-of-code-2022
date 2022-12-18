package solution

import arrow.core.Either
import arrow.core.right
import kotlin.math.max
import kotlin.math.min

object Solution14 : Solution {
    private const val SIZE = 1000
    private val SAND_SOURCE = Coord(500, 0)

    private data class Coord(val x: Int, val y: Int)
    private data class Line(val coords: List<Coord>)
    private class Field(val coords: Array<IntArray>) {
        operator fun get(x: Int): IntArray = coords[x]
    }

    private fun toLine(ends: Pair<Coord, Coord>): Line {
        val (sx, sy) = ends.first
        val (ex, ey) = ends.second
        val vertical = sx == ex
        return (if (vertical) (min(sy, ey)..max(sy, ey)).map { y -> Coord(sx, y) }
        else (min(sx, ex)..max(sx, ex)).map { x -> Coord(x, sy) }).let { Line(it) }
    }

    private fun parse(input: Sequence<String>): Field {
        val arr = Array(SIZE) { IntArray(SIZE) }
        input.map { line ->
            val pairs = line.split(" -> ").map { it.split(",") }
            pairs.map { (x, y) -> Coord(x.toInt(), y.toInt()) }
                .zipWithNext()
                .map { toLine(it) }
        }
            .forEach { rock ->
                for (line in rock) {
                    for ((x, y) in line.coords) {
                        arr[x][y] = 1
                    }
                }
            }
        return Field(arr)
    }

    private fun simulate(sand: Coord, field: Field): Coord {
        val (x, y) = sand
        return when {
            field.coords[x][y + 1] == 0 -> Coord(x, y + 1)
            field.coords[x - 1][y + 1] == 0 -> Coord(x - 1, y + 1)
            field.coords[x + 1][y + 1] == 0 -> Coord(x + 1, y + 1)
            else -> Coord(x, y)
        }
    }

    private fun solve(field: Field): Int {
        var count = 0
        var sand = SAND_SOURCE
        while (sand.x in 0 until (SIZE - 1) && sand.y in 0 until (SIZE - 1)) {
            val nextSand = simulate(sand, field)
            if (nextSand == SAND_SOURCE) return count + 1
            if (sand == nextSand) {
                field[sand.x][sand.y] = 1
                sand = SAND_SOURCE
                count++
            } else {
                sand = nextSand
            }
        }
        return count
    }

    private fun print(field: Field) {
        field.coords.flatMap { it.toList() }
            .filter { it == 1 }
            .size
            .also { println(it) }
    }

    override fun solve1(input: Sequence<String>): Either<String, Number> =
        solve(parse(input)).right()

    override fun solve2(input: Sequence<String>): Either<String, Number> {
        val field = parse(input)
        val lowestPoint = field.coords.indices
            .flatMap { x -> field.coords[0].indices.map { x to it } }
            .filter { (x, y) ->
                field[x][y] == 1
            }
            .maxBy { it.second }
            .second
        repeat(field.coords[0].size) {
            field[it][lowestPoint + 2] = 1
        }
        return solve(field).right()
    }
}
