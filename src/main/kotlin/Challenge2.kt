import java.io.BufferedReader
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class PokemonMap(val listPokemon: MutableList<String>, var map: String)

class Challenge2 {
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
        var line = reader.readLine()
        val limit100match = "([1-9][0-9]?|100)"
        val matchDescription = Regex("(50|[1-4][0-9]|[1-9]) +" + limit100match + " +" + limit100match + " *$")
        if (line != null) {
            val descInfo = matchDescription.find(line)
            if (descInfo != null) {
                val (nPokemon, nRow, nCol) = descInfo.destructured
                return try {
                    var listPokemon = readPokemons(nPokemon.toInt())
                    var map = readMap(nRow.toInt())
                    var resultMap = catchPokemon(listPokemon, map)
                    outFileObject.appendText("Case #" + nCase + ": " + resultMap + "\n")
                    true
                } catch (e: Exception) {
                    false
                }
            }
        }
        return false
    }

    //Read lines that have Pokemon names
    private fun readPokemons(nPokemon: Int): MutableList<String> {
        var list = mutableListOf<String>()
        for (pokemon in 1..nPokemon) {
            val line = reader.readLine()
            if (line.matches("^[A-Z]*$".toRegex())) list.add(line)
            else throw Exception()
        }
        return list
    }

    //Read lines of the map and convert them into a single string
    private fun readMap(nRow: Int): String {
        var map = ""
        for (row in 1..nRow) {
            var line = reader.readLine().replace(" ", "")
            if (line.matches("^[A-Z]*$".toRegex())) map += line
            else throw Exception()
        }
        return map
    }

    private fun catchPokemon(list: MutableList<String>, map: String): String {
        var listPokemon = list
        var pokemonMap = PokemonMap(listPokemon, map)
        while (!pokemonMap.listPokemon.isEmpty()) {
            // search from left to right
            pokemonMap = searchInMap(pokemonMap)
            if (!pokemonMap.listPokemon.isEmpty()) {
                //from right to left
                pokemonMap.map = pokemonMap.map.reversed()
                pokemonMap = searchInMap(pokemonMap)
                pokemonMap.map = pokemonMap.map.reversed()
            }
        }
        return pokemonMap.map
    }

    //Find and remove Pokemons from the map
    private fun searchInMap(pokemonMap: PokemonMap): PokemonMap {
        var listPokemon = pokemonMap.listPokemon.toMutableList()
        var map = pokemonMap.map
        for (pokemon in pokemonMap.listPokemon) {
            if (map.contains(pokemon)) {
                listPokemon.remove(pokemon)
                map = map.replace(pokemon, "")
            }
        }
        return PokemonMap(listPokemon, map)
    }
}