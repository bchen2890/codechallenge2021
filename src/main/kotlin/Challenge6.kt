import java.io.BufferedReader
import java.io.File
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.time.format.TextStyle
import java.util.*

val languageMap = mapOf(
    "CZ" to "cs", "DE" to "de", "DK" to "da", "EN" to "en", "ES" to "es",
    "FI" to "fi", "FR" to "fr", "IS" to "is", "GR" to "el", "HU" to "hu",
    "IT" to "it", "NL" to "nl", "VI" to "vi", "PL" to "pl", "RO" to "ro",
    "RU" to "ru", "SE" to "sv", "SI" to "sl", "SK" to "sk", "CA" to "ca"
)

//Some weekday names provided by LocalDate does not correspond to the test results
val specialName = mapOf(
    "maanantaina" to "maanantai", "tiistaina" to "tiistai",
    "keskiviikkona" to "keskiviikko", "torstaina" to "torstai",
    "lauantaina" to "lauantai", "sunnuntaina" to "sunnuntai", "perjantaina" to "perjantai",
    "marți" to "marţi"
)

class Challenge6 {
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
        val matchDescription = Regex("([\\d-]{10}):([A-Z]{2})$")
        if (line != null) {
            val descInfo = matchDescription.find(line)
            if (descInfo != null) {
                val (date, country) = descInfo.destructured
                val formatDate = date.replace(
                    Regex("(\\d{2})-(\\d{2})-(\\d{4})"), "$3-$2-$1"
                )
                var result = try {
                    val language = languageMap[country]
                    if (language == null) "INVALID_LANGUAGE"
                    else getWeekdayName(formatDate, language)
                } catch (e: DateTimeParseException) {
                    "INVALID_DATE"
                } catch (e: Exception) {
                    println(e)
                    return false
                }
                outFileObject.appendText("Case #$nCase: " + result + "\n")
                return true
            }
        }
        return false
    }

    private fun getWeekdayName(date: String, language: String): String {
        val weekDay = LocalDate.parse(date).dayOfWeek
            .getDisplayName(TextStyle.FULL, Locale(language))
        return checkSpecialName(weekDay.lowercase())
    }

    private fun checkSpecialName(name: String): String {
        var transformName = name
        var special = specialName[name]
        if (special != null) transformName = special
        return transformName
    }

}