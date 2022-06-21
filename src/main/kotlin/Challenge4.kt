import java.io.BufferedReader
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

const val SCALE_SIZE = 7
val ALL_NOTE = listOf("A", "A#", "B", "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#")
val ALL_NOTE_COMPLETE = arrayOf(
    arrayOf("A"), arrayOf("A#", "Bb"), arrayOf("B"), arrayOf("C", "B#"),
    arrayOf("C#", "Db"), arrayOf("D"), arrayOf("D#", "Eb"), arrayOf("E"), arrayOf("F", "E#"),
    arrayOf("F#", "Gb"), arrayOf("G"), arrayOf("G#", "Ab")
)

class Challenge4 {
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

        //Process cases
        for (nCase in 1..totalCase) {
            if (!processCase(nCase)) {
                println("Case #" + nCase + " does not has the correct format")
                break
            }
        }
    }

    private fun processCase(nCase: Int): Boolean {
        val startNote = reader.readLine()
        if (startNote != null && startNote.length < 3) {
            val jumpSequence = reader.readLine()
            if (jumpSequence != null) {
                return try {
                    outFileObject.appendText(
                        "Case #"
                                + nCase + ": " + getNoteSequence(startNote, jumpSequence) + "\n"
                    )
                    true
                } catch (e: Exception) {
                    false
                }
            }
        }
        return false
    }

    //Transform flat note to the sharp for the note before it
    private fun transformToSharp(note: String): String {
        val previousLetter = ((note.first().code - 'A'.code +
                SCALE_SIZE - 1) % SCALE_SIZE + 'A'.code).toChar()
        return note.replace(
            Regex("[A-G]b"), previousLetter + "#"
        )
    }

    //Get the next letter in [A..G]
    private fun nextLetter(letter: Char): Char {
        return ((letter.code - 'A'.code + 1) % SCALE_SIZE + 'A'.code).toChar()
    }

    //Return a scale with all the notes without sharps or flats
    private fun getInitialScale(rootNote: String): String {
        var scale = ""
        var note = rootNote.first()
        for (i in 1..SCALE_SIZE) {
            scale += nextLetter(note)
            note++
        }
        return scale
    }

    private fun getNoteSequence(note: String, jumpSequence: String): String {
        var scale = getScale(note, jumpSequence)
        if (scale.contains("null")) {
            //This version of the root note can't get a correct scale
            //Get the other version
            var iNote = ALL_NOTE.indexOf(note)
            var changeNote = ALL_NOTE_COMPLETE[iNote].find { !it.contains(note) }
            scale = getScale(changeNote!!, jumpSequence)
        }
        return scale
    }

    private fun getScale(note: String, jumpSequence: String): String {
        var initScale = getInitialScale(note)
        var scale = note
        var transformNote = if (note.contains('b')) transformToSharp(note)
                else if (note == "E#") "F"
                else if (note == "B#") "C"
                else note
        var iNote = ALL_NOTE.indexOf(transformNote)
        for (i in 0 until 7) {
            var jump = jumpSequence[i]
            var letter = initScale[i]
            var step = if (jump == 'T') 2 else 1
            iNote = (iNote + step) % ALL_NOTE.size
            scale += ALL_NOTE_COMPLETE[iNote].find { it.contains(letter) }
        }
        return scale
    }
}