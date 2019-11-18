import java.io.File
import java.io.PrintStream
import java.lang.RuntimeException
import java.util.*

object Main {
    @JvmStatic
    fun main(args: Array<String>) {
        val text = File("src.txt").readText()


        val punctuationMarks = hashSetOf('!', '?', ',', ';', '.', ':', '«', '(', ')', '»')
        var prepared = text
            .asSequence()
            .filter { it !in punctuationMarks }
            .joinToString(separator = "") { it.toLowerCase().toString() }

        File("prepared.txt").writeText(prepared)
        val log = PrintStream(File("log.txt"))

        val words = prepared
            .split("\\s".toRegex())
            .map { it.trim() }
            .filter { it.isNotBlank() }
            .toMutableList()
        log.println("Words count: ${words.size}")
        log.println("Individual words count: ${words.distinct().size}\n")

        for (w in words.distinct()) {
            log.println("$w = ${words.count { it == w }}")
        }

        val dictionary = readDictionary()

        var count = 0
        val iter = words.iterator()
        while (iter.hasNext()) {
            val word = iter.next()
            if (word in dictionary) {
                count++
                dictionary[word] = dictionary[word]!! - 1
                iter.remove()
            }
        }

        log.println("\n\n\n\nDictionary matches count: $count")

        for (entry in dictionary) {
            if (entry.value < 0) throw RuntimeException("kek")
        }

        dictionary.entries.removeIf { it.value <= 0 }

        val sortedDict = dictionary.toList().toMutableList()
        sortedDict.sortBy { it.second }

        log.println()
        val iter1 = words.iterator()
        out@while (iter1.hasNext()) {
            val word = iter1.next()
            var distance = 3
            var replacement: String? = null
            var j = 0
            for (i in 0 until sortedDict.size) {
                val target = sortedDict[i].first
                val tmp = distance(word, target)
                if (tmp <= distance && tmp <= 2) {
                    distance = tmp
                    replacement = target
                    j = i
                }
            }
            if (distance >= 3) {
                log.println("$word - не найдено - >2")
            } else {
                log.println("$word - $replacement - $distance")
                prepared = prepared.replaceFirst(word, replacement!!)
                if (sortedDict[j].second <= 1) {
                    sortedDict.removeAt(j)
                } else {
                    sortedDict[j] = Pair(sortedDict[j].first, sortedDict[j].second - 1)
                }
                sortedDict.sortBy { it.second }
            }
        }

        log.println("\n\n\n\n\n\n\n")
        logStatisticData(prepared, log)
    }

    private fun readDictionary(): MutableMap<String, Int> {
        val result = hashMapOf<String, Int>()
        val input = Scanner(File("dict.txt"))

        while (input.hasNext()) {
            result[input.next()] = input.nextInt()
        }

        return result
    }

    private fun logStatisticData(prepared: String, log: PrintStream) {
        val words = prepared.split("\\s".toRegex()).map { it.trim() }.filter { it.isNotBlank() }.toMutableList()
        log.println("Words count: ${words.size}")
        log.println("Individual words count: ${words.distinct().size}\n")

        for (w in words.distinct()) {
            log.println("$w = ${words.count { it == w }}")
        }

        val dictionary = readDictionary()

        var count = 0
        val iter = words.iterator()
        while (iter.hasNext()) {
            val word = iter.next()
            if (word in dictionary) {
                count++
            }
        }

        log.println("Dictionary matches count: $count")

    }

    private fun distance(str1: String, str2: String): Int {
        val arr = IntArray(str2.length + 1)
        val arr1 = IntArray(str2.length + 1)

        for (j in 0..str2.length) {
            arr1[j] = j
        }

        for (i in 1..str1.length) {
            System.arraycopy(arr1, 0, arr, 0, arr.size)

            arr1[0] = i
            for (j in 1..str2.length) {
                val cost = if (str1[i - 1] != str2[j - 1]) 1 else 0
                arr1[j] = min(
                    arr[j] + 1,
                    arr1[j - 1] + 1,
                    arr[j - 1] + cost
                )
            }
        }

        return arr1[arr1.size - 1]
    }

    private fun min(n1: Int, n2: Int, n3: Int): Int {
        return Math.min(Math.min(n1, n2), n3)
    }


}