import java.io.BufferedReader
import java.io.File
import java.nio.charset.Charset
import java.text.Normalizer
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class Challenge5 {
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
        reader = inFileObject.bufferedReader(Charset.forName("ISO_8859_1"))
       /* var totalCase = 0
        try {
            totalCase = reader.readLine().toInt()
        } catch (e: NumberFormatException) {
            println("The number of cases does not has the correct format")
            return
        }*/
        for (line in reader.lines()) {
            //println(line.replace(Regex("[\\w\\d,/.]*"),""))
            line.replace(Regex("[\\w\\d,/.]*"),"").toCharArray()
                .forEachIndexed { pos, c ->
                    println("$pos. $c -> ${String.format("\\%x", c.toInt())}")
                }
            /*val value = Regex("U.E[0-9]{4}").find(line)?.value
            if(value!=null){
                println(value)
            outFileObject.appendText(value+" -> \n")
            }*/
        }
        /*//Process cases
        for (nCase in 1..totalCase) {
            if (!processCase(nCase)) {
                println("Case #" + nCase + " does not has the correct format")
                break
            }
        }*/
    }

    private fun processCase(nCase: Int): Boolean {

        return false
    }
}