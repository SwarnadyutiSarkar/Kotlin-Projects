import io.ktor.application.*
import io.ktor.http.cio.websocket.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import java.util.*

data class Document(val id: String, var content: String)

val documents = mutableMapOf<String, Document>()

fun main() {
    embeddedServer(Netty, port = 8080) {
        install(io.ktor.websocket.WebSockets)
        routing {
            route("/document/{id}") {
                val documentId = UUID.randomUUID().toString()
                val document = Document(documentId, "")

                documents[documentId] = document

                webSocket {
                    try {
                        send(Frame.Text(document.content))

                        for (frame in incoming) {
                            if (frame is Frame.Text) {
                                val newText = frame.readText()
                                document.content = newText
                                broadcast(documentId, newText)
                            }
                        }
                    } catch (e: ClosedReceiveChannelException) {
                        println("Connection closed: ${e.message}")
                    } finally {
                        documents.remove(documentId)
                    }
                }
            }
        }
    }.start(wait = true)
}

suspend fun WebSocketSession.broadcast(documentId: String, newText: String) {
    for (session in sessions) {
        if (session != this) {
            session.send(Frame.Text(newText))
        }
    }
}
