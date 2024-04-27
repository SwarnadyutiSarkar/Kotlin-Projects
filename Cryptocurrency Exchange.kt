import java.util.*

data class User(val id: String, var balance: Double)

data class Order(val id: String, val userId: String, val type: OrderType, val amount: Double, val price: Double)

enum class OrderType {
    BUY, SELL
}

class Exchange {
    private val users = mutableMapOf<String, User>()
    private val buyOrders = PriorityQueue<Order> { o1, o2 -> if (o1.price > o2.price) -1 else 1 }
    private val sellOrders = PriorityQueue<Order> { o1, o2 -> if (o1.price < o2.price) -1 else 1 }

    fun registerUser(userId: String) {
        if (!users.containsKey(userId)) {
            users[userId] = User(userId, 0.0)
        }
    }

    fun deposit(userId: String, amount: Double) {
        require(users.containsKey(userId)) { "User not found" }
        users[userId]?.balance = users[userId]?.balance?.plus(amount) ?: amount
    }

    fun placeOrder(userId: String, type: OrderType, amount: Double, price: Double) {
        require(users.containsKey(userId)) { "User not found" }

        val orderId = UUID.randomUUID().toString()
        val order = Order(orderId, userId, type, amount, price)

        when (type) {
            OrderType.BUY -> {
                buyOrders.add(order)
            }
            OrderType.SELL -> {
                sellOrders.add(order)
            }
        }
        matchOrders()
    }

    private fun matchOrders() {
        while (buyOrders.isNotEmpty() && sellOrders.isNotEmpty()) {
            val buyOrder = buyOrders.peek()
            val sellOrder = sellOrders.peek()

            if (buyOrder.price >= sellOrder.price) {
                if (buyOrder.amount >= sellOrder.amount) {
                    executeTrade(buyOrder, sellOrder)
                    buyOrders.poll()
                    sellOrders.poll()
                } else {
                    executeTrade(buyOrder.copy(amount = sellOrder.amount), sellOrder)
                    buyOrder.amount -= sellOrder.amount
                    sellOrders.poll()
                }
            } else {
                break
            }
        }
    }

    private fun executeTrade(buyOrder: Order, sellOrder: Order) {
        val buyUser = users[buyOrder.userId]
        val sellUser = users[sellOrder.userId]

        requireNotNull(buyUser) { "Buyer not found" }
        requireNotNull(sellUser) { "Seller not found" }

        val totalCost = sellOrder.price * sellOrder.amount
        require(buyUser.balance >= totalCost) { "Insufficient balance for buyer" }

        buyUser.balance -= totalCost
        sellUser.balance += totalCost

        println("Trade executed: ${sellOrder.amount} units at ${sellOrder.price} ${sellOrder.type} from ${sellOrder.userId} to ${buyOrder.userId}")
    }

    fun displayBalances() {
        for ((userId, user) in users) {
            println("User: $userId, Balance: ${user.balance}")
        }
    }
}

fun main() {
    val exchange = Exchange()

    exchange.registerUser("Alice")
    exchange.registerUser("Bob")

    exchange.deposit("Alice", 100.0)
    exchange.deposit("Bob", 100.0)

    exchange.placeOrder("Alice", OrderType.BUY, 5.0, 20.0)
    exchange.placeOrder("Bob", OrderType.SELL, 5.0, 20.0)

    exchange.displayBalances()
}
