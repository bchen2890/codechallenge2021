import java.io.*
import java.net.Socket
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

//Coordinates
data class Coord (var x: String, var y: String)

class Challenge7 {
    private lateinit var outFileObject: File
    var socket: Socket? = null
    private lateinit var writer: PrintWriter
    private lateinit var reader: BufferedReader

    //Orders
    private val NORTH = "north"
    private val SOUTH = "south"
    private val EAST = "east"
    private val WEST = "west"
    private val LOOK = "look"
    private val IS_EXIT = "is exit?"
    private val GO_TO = "go to "

    //Possible movements
    private val dir = arrayOf(WEST,NORTH, SOUTH,EAST)

    //Get possible movements from current position
    private fun getDir():MutableList<String>{
        writer.println(LOOK)
        val line =  reader.readLine()
        var possibleDir = mutableListOf<String>()
        for (i in 0 until 4)
            if(line.contains(dir[i])) possibleDir.add(dir[i])
        return possibleDir
    }

    //Move to the direction and get the new position
    private fun moveTo (dir:String):Coord {
        writer.println(dir)
        return try {
            val (x,y) = Regex("\\((\\d+), (\\d+)\\)").find(reader.readLine())!!.destructured
            Coord(x,y)
        } catch(e:NullPointerException){
            Coord("","")
        }
    }

    //Check if the current position is the exit
    private fun isExit():Boolean{
        writer.println(IS_EXIT)
        return !reader.readLine().contains("No")
    }

    //Go to the specific position
    private fun goTo(coord:Coord){
        writer.println(GO_TO+coord.x+","+coord.y)
        reader.readLine()
    }

    //write path to file
    private fun printPath(sol:MutableList<Coord>) {
        var output = ""
        for (i in 0 until sol.size-1) {
            val c = sol[i]
            output+="(${c.x}, ${c.y}), "
        }
        val c = sol[sol.size-1]
        output+="(${c.x}, ${c.y})"
        outFileObject.writeText(output)
    }

    //Get the shortest path
    private fun getPath (end:Coord, parents:MutableMap<Coord,Coord>): MutableList<Coord> {
        var list = mutableListOf<Coord>()
        var next = end
        list.add(end)
        while (next!=Coord("0","0")){
            next = parents[next]!!
            list.add(next)
        }
        return list.asReversed()
    }

    private fun bfs (start:Coord):Boolean{
        val q: Queue<Coord> = LinkedList()
        val visited = mutableListOf<Coord>()
        var parentNode = mutableMapOf<Coord,Coord>()
        goTo(start)
        q.add(start)
        visited.add(start)
        while (q.isNotEmpty()){
            var get:Coord = q.poll()
            goTo(get)
            if ( isExit() ){
                printPath(getPath(get,parentNode))
                break
            }
            for (k in getDir()) {
                val move = moveTo(k)
                // check if it is possible to go to the direction
                if (move.x!="" && !visited.contains(move)) {
                    visited.add(move)
                    q.add(move)
                    parentNode[move] = get
                    goTo(get)
                }
            }
        }
        return true
    }

    fun getOutput() {
        //create output file
        val now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss"))
        val outFileName = "src/output/C7_output_" +
                now.toString() + ".txt"
        outFileObject = File(outFileName)
        if (!outFileObject.createNewFile()) {
            println("$outFileName already exists.")
            return
        }
        //Connect socket
        try {
            socket = Socket("51.103.36.164", 4321)
            writer = PrintWriter(socket!!.getOutputStream(), true)
            reader = BufferedReader(InputStreamReader(socket!!.getInputStream()))
        } catch (e: IOException) {
            println(e)
            return
        } catch (e: Exception){
            println(e)
        }
        reader!!.readLine()
        reader!!.readLine()
        val find = bfs(Coord("0","0"))
        if (!find) {
            println("Exit cannot be reached")
        }
    }
}