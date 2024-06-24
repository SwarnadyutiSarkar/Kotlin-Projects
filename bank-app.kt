fun main() {
    val account = BankAccount("John Doe", 12345, 1000.0)
    
    println("Welcome, ${account.ownerName}!")
    println("Account Number: ${account.accountNumber}")
    println("Initial Balance: \$${account.balance}")

    account.deposit(500.0)
    println("Balance after deposit: \$${account.balance}")

    account.withdraw(200.0)
    println("Balance after withdrawal: \$${account.balance}")

    try {
        account.withdraw(1500.0)
    } catch (e: IllegalArgumentException) {
        println(e.message)
    }
}

class BankAccount(val ownerName: String, val accountNumber: Int, initialBalance: Double) {
    var balance: Double = initialBalance
        private set

    fun deposit(amount: Double) {
        if (amount > 0) {
            balance += amount
            println("Deposited \$${amount}. New balance is \$${balance}.")
        } else {
            println("Deposit amount must be positive.")
        }
    }

    fun withdraw(amount: Double) {
        when {
            amount <= 0 -> println("Withdrawal amount must be positive.")
            amount > balance -> throw IllegalArgumentException("Insufficient funds. Cannot withdraw \$${amount}.")
            else -> {
                balance -= amount
                println("Withdrew \$${amount}. New balance is \$${balance}.")
            }
        }
    }
}
