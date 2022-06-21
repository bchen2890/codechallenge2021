import java.io.BufferedReader
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class LetterType(val letter: Char, var value: Double)

class Challenge3 {
    private lateinit var reader: BufferedReader
    private lateinit var outFileObject: File

    fun getOutput(inFileName: String) {
        //check if exists input file
        var inFileObject = File(inFileName)
        if (!inFileObject.exists()) {
            print("$inFileName file does not exist.")
            return
        }

        //create output file
        val now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss"))
        val outFileName = "src/output/" + inFileObject.nameWithoutExtension + "_output_" +
                now.toString() + ".txt"
        outFileObject = File(outFileName)
        if (!outFileObject.createNewFile()) {
            println("$outFileName already exists.")
            return
        }

        //read number of cases
        reader = inFileObject.bufferedReader()
        var totalCase = 0
        try {
            totalCase = reader.readLine().toInt()
        } catch (e: NumberFormatException) {
            println("The number of cases does not has the correct format")
            return
        }

        //process cases
        for (nCase in 1..totalCase) {
            if (!processCase(nCase)) {
                println("Case #" + nCase + " does not has the correct format")
                break
            }
        }
    }

    private fun processCase(nCase: Int): Boolean {
        var line = reader.readLine()
        val matchDescription = Regex("(\\w+)-(\\w+)\\|(.*)$")
        if (line != null) {
            val descInfo = matchDescription.find(line)
            if (descInfo != null) {
                val (firstW, secondW, stringOfList) = descInfo.destructured
                return try {
                    var listLetter = readMapLetter(stringOfList)
                    var firstScore = getWordScore(listLetter, firstW)
                    var secondScore = getWordScore(listLetter, secondW)
                    var winWord = firstW
                    if (firstScore < secondScore) winWord = secondW
                    else if (firstScore.equals(secondScore)) winWord = "-"
                    outFileObject.appendText("Case #" + nCase + ": " + winWord + "\n")
                    true
                } catch (e: Exception) {
                    println(e)
                    false
                }
            }
        }
        return false
    }

    //Read and convert list of letters into map
    private fun readMapLetter(stringOfList: String): Map<Char, Double> {
        val valueMatch = "(\\d+/\\d+|\\d+)"
        var charMatch = when (stringOfList.first()) {
            '{' -> "\'(\\w)\': " //dictionary
            '[' -> "\'(\\w)\', " //list of tuples
            else -> "(\\w)=" //list of assignments
        }
        val match = Regex(charMatch + valueMatch)
        val listLetter = getListLetters(match, stringOfList)
        return listLetter.associate { it.letter to it.value }
    }

    //Match and get list of letters
    private fun getListLetters(match: Regex, stringOfList: String): List<LetterType> {
        var allMatch = match.findAll(stringOfList)
        var list = mutableListOf<LetterType>()
        for (match in allMatch) {
            var (letter, value) = match.destructured
            list.add(LetterType(letter.first(), convertRatio(value)))
        }
        return list
    }

    private fun convertRatio(ratio: String): Double {
        return if (ratio.contains("/")) {
            val rat = ratio.split("/").toTypedArray()
            rat[0].toDouble() / rat[1].toDouble()
        } else {
            ratio.toDouble()
        }
    }

    private fun getWordScore(mapLetter: Map<Char, Double>, word: String): Double {
        var score = 0.0
        for (letter in word) {
            score += mapLetter[letter]!!
        }
        return score
    }
}