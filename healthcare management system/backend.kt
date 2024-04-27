import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.request.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

data class Patient(val id: String, val name: String, val dob: String, val gender: String, val medicalHistory: String = "")

val patients = mutableListOf<Patient>()

fun main() {
    embeddedServer(Netty, port = 8080) {
        install(ContentNegotiation) {
            gson {}
        }
        routing {
            get("/patients") {
                call.respond(patients)
            }
            post("/patients") {
                val patient = call.receive<Patient>()
                patients.add(patient)
                call.respond(HttpStatusCode.OK, "Patient added successfully")
            }
            post("/patients/{id}/medical-history") {
                val patientId = call.parameters["id"]
                val medicalHistory = call.receive<String>()
                val patient = patients.find { it.id == patientId }
                if (patient != null) {
                    patient.medicalHistory = medicalHistory
                    call.respond(HttpStatusCode.OK, "Medical history updated successfully")
                } else {
                    call.respond(HttpStatusCode.NotFound, "Patient not found")
                }
            }
        }
    }.start(wait = true)
}
