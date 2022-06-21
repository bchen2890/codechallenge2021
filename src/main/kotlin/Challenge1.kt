import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class Challenge1 {
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
        var outFileObject = File(outFileName)
        if (!outFileObject.createNewFile()) {
            println("$outFileName already exists.")
            return
        }

        //read and write cases
        val reader = inFileObject.bufferedReader()
        var totalCase = 0
        try {
            totalCase = reader.readLine().toInt()
        } catch (e: NumberFormatException) {
            println("The number of cases does not has the correct format")
            return
        }
        val match = Regex("([1-6]):([1-6])$")
        var numCase = 1
        for (line in reader.lines()) {
            val diceValue = match.find(line)
            if (diceValue != null) {
                val (firstDice, secondDice) = diceValue.destructured
                outFileObject.appendText("Case #" + numCase + ": " + getMinScore(firstDice, secondDice) + "\n")
            } else {
                println("Line " + numCase + 1 + " does not has the correct format")
            }
            numCase++
        }
    }

    private fun getMinScore(firstDice: String, secondDice: String): String {
        val firstScore = firstDice.toInt() + secondDice.toInt()
        return if (firstScore < 12) (firstScore + 1).toString() else "-"
    }
}