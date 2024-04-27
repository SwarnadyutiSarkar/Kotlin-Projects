import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.request.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

data class Event(val id: String, val name: String, val date: String, val location: String, val capacity: Int, val registeredAttendees: MutableList<String> = mutableListOf())

val events = mutableListOf<Event>()

fun main() {
    embeddedServer(Netty, port = 8080) {
        install(ContentNegotiation) {
            gson {}
        }
        routing {
            get("/events") {
                call.respond(events)
            }
            post("/events") {
                val event = call.receive<Event>()
                events.add(event)
                call.respond(HttpStatusCode.OK, "Event created successfully")
            }
            post("/events/{id}/register") {
                val eventId = call.parameters["id"]
                val event = events.find { it.id == eventId }
                if (event != null) {
                    if (event.registeredAttendees.size < event.capacity) {
                        event.registeredAttendees.add("User") // For simplicity, assume registration for a hardcoded user
                        call.respond(HttpStatusCode.OK, "Registered successfully for event ${event.name}")
                    } else {
                        call.respond(HttpStatusCode.BadRequest, "Event is full")
                    }
                } else {
                    call.respond(HttpStatusCode.NotFound, "Event not found")
                }
            }
        }
    }.start(wait = true)
}
