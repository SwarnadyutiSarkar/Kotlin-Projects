import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

data class User(val id: String, val username: String, val password: String)

data class Course(val id: String, val name: String, val content: String)

val users = mutableListOf<User>()
val courses = mutableListOf<Course>()

fun main() {
    embeddedServer(Netty, port = 8080) {
        install(ContentNegotiation) {
            gson {}
        }
        routing {
            post("/register") {
                val user = call.receive<User>()
                users.add(user)
                call.respond(HttpStatusCode.OK, "User registered successfully")
            }
            post("/login") {
                val credentials = call.receive<User>()
                val user = users.find { it.username == credentials.username && it.password == credentials.password }
                if (user != null) {
                    call.respond(HttpStatusCode.OK, "Login successful")
                } else {
                    call.respond(HttpStatusCode.Unauthorized, "Invalid credentials")
                }
            }
            get("/courses") {
                call.respond(courses)
            }
            get("/courses/{id}") {
                val courseId = call.parameters["id"]
                val course = courses.find { it.id == courseId }
                if (course != null) {
                    call.respond(course)
                } else {
                    call.respond(HttpStatusCode.NotFound, "Course not found")
                }
            }
        }
    }.start(wait = true)
}
