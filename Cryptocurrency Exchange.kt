import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.PriorityBlockingQueue

data class Cryptocurrency(val name: String, var price: Double)

data class Order(val id: Int, val userId: Int, val type: OrderType, val amount: Double, val price: Double) : Comparable<Order> {
    override fun compareTo(other: Order): Int {
        return when (type) {
            OrderType.BUY -> other.price.compareTo(price) // Higher price buys first
            OrderType.SELL -> price.compareTo(other.price) // Lower price sells first
        }
    }
}

enum class OrderType {
    BUY, SELL
}

class OrderBook {
    private val buyOrders = PriorityBlockingQueue<Order>()
    private val sellOrders = PriorityBlockingQueue<Order>()

    fun placeOrder(order: Order) {
        when (order.type) {
            OrderType.BUY -> buyOrders.add(order)
            OrderType.SELL -> sellOrders.add(order)
        }
        matchOrders()
    }

    private fun matchOrders() {
        while (buyOrders.isNotEmpty() && sellOrders.isNotEmpty()) {
            val buyOrder = buyOrders.peek()
            val sellOrder = sellOrders.peek()

            if (buyOrder.price >= sellOrder.price) {
                val tradeAmount = minOf(buyOrder.amount, sellOrder.amount)
                println("Executing trade: Buy ${tradeAmount} units at ${sellOrder.price}")

                buyOrders.poll()
                sellOrders.poll()
            } else {
                break
            }
        }
    }
}

class User(val id: Int, val balance: Double)

class CryptocurrencyExchange {
    private val cryptocurrencies = ConcurrentHashMap<String, Cryptocurrency>()
    private val orderBook = OrderBook()
    private val users = ConcurrentHashMap<Int, User>()

    fun addCryptocurrency(name: String, price: Double) {
        cryptocurrencies[name] = Cryptocurrency(name, price)
    }

    fun updatePrice(name: String, price: Double) {
        cryptocurrencies[name]?.price = price
    }

    fun placeOrder(userId: Int, type: OrderType, cryptocurrencyName: String, amount: Double, price: Double) {
        val user = users[userId] ?: throw IllegalArgumentException("User not found")
        val cryptocurrency = cryptocurrencies[cryptocurrencyName] ?: throw IllegalArgumentException("Cryptocurrency not found")

        // Check if the user has enough balance for BUY orders (simplified check for demonstration purposes)
        if (type == OrderType.BUY && user.balance < price * amount) {
            throw IllegalArgumentException("Insufficient balance")
        }

        val orderId = (1..1000).random() // Simple random order ID generator
        val order = Order(orderId, userId, type, amount, price)
        orderBook.placeOrder(order)

        // For simplicity, we're not updating the user's balance or handling more complex scenarios here
    }
}

fun main() {
    val exchange = CryptocurrencyExchange()

    // Add some cryptocurrencies
    exchange.addCryptocurrency("Bitcoin", 50000.0)
    exchange.addCryptocurrency("Ethereum", 3000.0)

    // Create a user
    val user1 = User(id = 1, balance = 100000.0)
    val user2 = User(id = 2, balance = 100000.0)
    exchange.users[user1.id] = user1
    exchange.users[user2.id] = user2

    // Place some orders
    exchange.placeOrder(userId = 1, type = OrderType.BUY, cryptocurrencyName = "Bitcoin", amount = 1.0, price = 50000.0)
    exchange.placeOrder(userId = 2, type = OrderType.SELL, cryptocurrencyName = "Bitcoin", amount = 1.0, price = 50000.0)
}
