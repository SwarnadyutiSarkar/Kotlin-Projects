import java.util.Scanner

fun main() {
    val scanner = Scanner(System.`in`)

    println("Hello! I'm your virtual assistant. How can I assist you today?")

    while (true) {
        print("> ")
        val input = scanner.nextLine()

        when {
            input.contains("hello", ignoreCase = true) -> {
                println("Hello! How can I help you?")
            }
            input.contains("how are you", ignoreCase = true) -> {
                println("I'm just a program, but thanks for asking!")
            }
            input.contains("tell me a joke", ignoreCase = true) -> {
                println("Why don't scientists trust atoms? Because they make up everything!")
            }
            input.contains("exit", ignoreCase = true) -> {
                println("Goodbye!")
                break
            }
            else -> {
                println("I'm sorry, I don't understand.")
            }
        }
    }
}
