package solution

import arrow.core.Either
import arrow.core.right
import java.util.*

object Solution07 : Solution {
    sealed class FS
    data class File(val size: Long, val name: String) : FS()
    data class Dir(val name: String, val fs: List<FS> = emptyList()) : FS()

    class Parser(private val tokens: Sequence<String>) {
        private val stack = LinkedList<Dir>()
        private val fileRegex = Regex("^[0-9]+ .*")
        private val startRegex = Regex("^\\\$ cd [a-zA-Z0-9-_/]+")
        private val endRegex = Regex("^\\\$ cd \\.\\.")
        private fun isFile(s: String): Boolean = s.matches(fileRegex)
        private fun isStart(s: String): Boolean = s.matches(startRegex)
        private fun isEnd(s: String): Boolean = s.matches(endRegex)
        private fun getFile(s: String): File = s.split(" ").let { (size, name) -> File(size.toLong(), name) }
        private fun getDir(s: String): Dir = Dir(s.split("cd ")[1])

        private fun mergeDirs() {
            val (childDir, childFiles) = stack.removeLast()
            val (parentDir, parentFiles) = stack.removeLast()
            val mergedDir = Dir(parentDir, parentFiles + Dir(childDir, childFiles))
            stack.add(mergedDir)
        }

        fun parse(): Dir {
            val iter = tokens.iterator()
            while (iter.hasNext()) {
                val token = iter.next()
                when {
                    isStart(token) -> stack.add(getDir(token))
                    isEnd(token) -> mergeDirs()
                    isFile(token) -> {
                        val (dir, files) = stack.removeLast()
                        stack.add(Dir(dir, files + getFile(token)))
                    }
                }
            }
            while (stack.size > 1) mergeDirs()
            return stack.pop()
        }
    }

    override fun solve1(input: Sequence<String>): Either<String, Number> {
        val dir = Parser(input).parse()
        var result = 0L
        fun go(fs: FS, limit: Long): Long =
            when (fs) {
                is Dir -> {
                    val sum = fs.fs.sumOf { go(it, limit) }
                    if (sum <= limit) result += sum
                    sum
                }
                is File -> fs.size
            }
        go(dir, 100_000)
        return result.right()
    }

    override fun solve2(input: Sequence<String>): Either<String, Number> {
        val dir = Parser(input).parse()
        val dirs = mutableListOf<Long>()
        val target = 70000000 - 30000000
        fun go(fs: FS): Long =
            when (fs) {
                is Dir -> {
                    val sum = fs.fs.sumOf { go(it) }
                    dirs.add(sum)
                    sum
                }
                is File -> fs.size
            }
        val used = go(dir)
        val goal = used - target
        return dirs.filter { it >= goal }.minBy { it - goal }.right()
    }
}
