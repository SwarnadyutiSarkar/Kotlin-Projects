import io.ktor.application.*
import io.ktor.features.*
import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.request.*
import io.ktor.gson.*
import io.ktor.sessions.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import java.util.concurrent.atomic.AtomicInteger
import java.util.UUID.randomUUID

data class User(val id: String = randomUUID().toString(), val username: String, val password: String)

data class Product(val id: String = randomUUID().toString(), val name: String, val price: Double)

val users = mutableListOf<User>()
val products = mutableListOf<Product>()

fun main() {
    embeddedServer(Netty, port = 8080) {
        install(ContentNegotiation) {
            gson {
                setPrettyPrinting()
            }
        }
        install(Sessions) {
            cookie<User>("SESSION")
        }
        routing {
            route("/api") {
                post("/register") {
                    val user = call.receive<User>()
                    users.add(user)
                    call.sessions.set(user)
                    call.respond(user)
                }
                post("/login") {
                    val credentials = call.receive<User>()
                    val user = users.find { it.username == credentials.username && it.password == credentials.password }
                    if (user != null) {
                        call.sessions.set(user)
                        call.respond(user)
                    } else {
                        call.respond(HttpStatusCode.Unauthorized, "Invalid credentials")
                    }
                }
                post("/logout") {
                    call.sessions.clear<User>()
                    call.respond(HttpStatusCode.OK, "Logged out successfully")
                }
                get("/products") {
                    call.respond(products)
                }
                post("/products") {
                    val product = call.receive<Product>()
                    products.add(product)
                    call.respond(product)
                }
            }
        }
    }.start(wait = true)
}
