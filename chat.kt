import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket
import java.util.concurrent.Executors

class ChatClient(private val serverAddress: String, private val serverPort: Int) {
    private val socket = Socket(serverAddress, serverPort)
    private val reader = BufferedReader(InputStreamReader(socket.getInputStream()))
    private val writer = PrintWriter(socket.getOutputStream(), true)
    
    fun sendMessage(message: String) {
        writer.println(message)
    }
    
    fun receiveMessage() {
        val message = reader.readLine()
        println("Received message: $message")
    }
    
    fun close() {
        socket.close()
    }
}

fun main() {
    val client = ChatClient("localhost", 12345)
    val executor = Executors.newFixedThreadPool(2)

    executor.submit {
        while (true) {
            client.receiveMessage()
        }
    }

    executor.submit {
        while (true) {
            val input = readLine() ?: ""
            client.sendMessage(input)
        }
    }
}
