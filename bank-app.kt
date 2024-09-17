import javafx.application.Application
import javafx.geometry.Insets
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.layout.BorderPane
import javafx.scene.layout.GridPane
import javafx.stage.Stage
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.ReadOnlyObjectWrapper
import java.lang.Exception

data class Account(val id: String, val name: String, private var balance: Double) {
    fun deposit(amount: Double) {
        if (amount > 0) balance += amount
    }

    fun withdraw(amount: Double) {
        if (amount > 0 && balance >= amount) balance -= amount
    }

    fun getBalance(): Double = balance
}

class BankApp : Application() {

    private val accounts: ObservableList<Account> = FXCollections.observableArrayList()

    override fun start(primaryStage: Stage) {
        primaryStage.title = "Bank Application"

        val borderPane = BorderPane()

        // Create TableView
        val tableView = TableView<Account>()
        tableView.items = accounts

        // Create columns
        val idColumn = TableColumn<Account, String>("ID").apply {
            cellValueFactory = PropertyValueFactory("id")
        }
        val nameColumn = TableColumn<Account, String>("Name").apply {
            cellValueFactory = PropertyValueFactory("name")
        }
        val balanceColumn = TableColumn<Account, Double>("Balance").apply {
            cellValueFactory = PropertyValueFactory("balance")
        }

        tableView.columns.addAll(idColumn, nameColumn, balanceColumn)

        // Create Form for Account Creation
        val idField = TextField()
        val nameField = TextField()
        val createButton = Button("Create Account").apply {
            setOnAction {
                val id = idField.text
                val name = nameField.text

                if (id.isNotBlank() && name.isNotBlank()) {
                    if (accounts.any { it.id == id }) {
                        showAlert(Alert.AlertType.WARNING, "Error", "Account with ID $id already exists")
                    } else {
                        accounts.add(Account(id, name, 0.0))
                        idField.clear()
                        nameField.clear()
                    }
                } else {
                    showAlert(Alert.AlertType.WARNING, "Form Error", "Please fill in all fields")
                }
            }
        }

        val createFormGrid = GridPane().apply {
            padding = Insets(10.0)
            hgap = 10.0
            vgap = 10.0

            add(Label("Account ID:"), 0, 0)
            add(idField, 1, 0)
            add(Label("Name:"), 0, 1)
            add(nameField, 1, 1)
            add(createButton, 1, 2)
        }

        // Create Form for Transactions
        val transactionIdField = TextField()
        val amountField = TextField()
        val depositButton = Button("Deposit").apply {
            setOnAction {
                handleTransaction(transactionIdField.text, amountField.text.toDoubleOrNull(), true)
            }
        }
        val withdrawButton = Button("Withdraw").apply {
            setOnAction {
                handleTransaction(transactionIdField.text, amountField.text.toDoubleOrNull(), false)
            }
        }

        val transactionFormGrid = GridPane().apply {
            padding = Insets(10.0)
            hgap = 10.0
            vgap = 10.0

            add(Label("Account ID:"), 0, 0)
            add(transactionIdField, 1, 0)
            add(Label("Amount:"), 0, 1)
            add(amountField, 1, 1)
            add(depositButton, 0, 2)
            add(withdrawButton, 1, 2)
        }

        borderPane.center = tableView
        borderPane.left = createFormGrid
        borderPane.right = transactionFormGrid

        val scene = Scene(borderPane, 600.0, 400.0)
        primaryStage.scene = scene
        primaryStage.show()
    }

    private fun handleTransaction(accountId: String, amount: Double?, isDeposit: Boolean) {
        if (amount == null || amount <= 0) {
            showAlert(Alert.AlertType.WARNING, "Transaction Error", "Invalid amount")
            return
        }

        val account = accounts.find { it.id == accountId }
        if (account == null) {
            showAlert(Alert.AlertType.WARNING, "Error", "Account not found")
            return
        }

        if (isDeposit) {
            account.deposit(amount)
        } else {
            try {
                account.withdraw(amount)
            } catch (e: Exception) {
                showAlert(Alert.AlertType.ERROR, "Withdrawal Error", "Insufficient funds")
                return
            }
        }

        // Refresh table to show updated balances
        (tableView.items as ObservableList<Account>).set(tableView.items.indexOf(account), account)
    }

    private fun showAlert(alertType: Alert.AlertType, title: String, content: String) {
        Alert(alertType).apply {
            this.title = title
            this.headerText = null
            this.contentText = content
            showAndWait()
        }
    }
}

fun main() {
    Application.launch(BankApp::class.java)
}
