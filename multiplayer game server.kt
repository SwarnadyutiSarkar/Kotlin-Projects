import io.ktor.application.*
import io.ktor.http.cio.websocket.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch

data class Player(val id: String, val name: String)

val players = mutableMapOf<String, Player>()

fun main() {
    embeddedServer(Netty, port = 8080) {
        install(io.ktor.websocket.WebSockets)
        routing {
            webSocket("/game") {
                val playerId = generatePlayerId()
                val player = Player(playerId, "Player $playerId")
                players[playerId] = player

                println("Player $playerId connected")

                try {
                    incoming.consumeEach { frame ->
                        if (frame is Frame.Text) {
                            val input = frame.readText()
                            println("Received from Player $playerId: $input")

                            // Handle player actions here

                            // Example: Echo back to the player
                            launch {
                                outgoing.send(Frame.Text(input))
                            }
                        }
                    }
                } catch (e: ClosedReceiveChannelException) {
                    println("Player $playerId disconnected")
                    players.remove(playerId)
                }
            }
        }
    }.start(wait = true)
}

fun generatePlayerId(): String {
    return java.util.UUID.randomUUID().toString()
}
