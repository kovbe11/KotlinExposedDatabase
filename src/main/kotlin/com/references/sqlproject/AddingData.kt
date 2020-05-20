package com.references.sqlproject

import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.event.EventHandler
import javafx.scene.control.SelectionMode
import javafx.scene.text.Text
import org.jetbrains.exposed.sql.transactions.transaction
import tornadofx.*
import java.util.*

class NewItemForm(val controller: DatabaseController) : Fragment("Add new Items") {

    val model = TempItem()
    val validators = LinkedList<ValidationContext.Validator<String>>()

    override val root = form {
        fieldset {
            field("id") {

                //TODO: rájönni a bindolás szintaxisára
                val textField = textfield {
                    textProperty().addListener(ChangeListener { _, _, newValue ->
                        model.id = newValue.toIntOrNull()
                    })
                }

                val validator = ValidationContext().addValidator(textField, textField.textProperty()) {
                    val item: Item? = transaction(DB.db) {
                        textField.text.toIntOrNull()?.let { Item.findById(it) }
                    }
                    when {
                        item != null -> {
                            error("There is already an item with this id")
                        }
                        else -> null
                    }

                }

                validators.add(validator)
            }

            field("internet code") {
                val textField = textfield {
                    textProperty().addListener(ChangeListener { _, _, newValue ->
                        model.ic = newValue
                    })
                }

                val validator = ValidationContext().addValidator(textField, textField.textProperty()) {
                    if (it.isNullOrBlank()) error("The internet code field is required") else null
                }
                validator.validate()
                validators.add(validator)
            }

            field("name") {
                val textField = textfield {
                    textProperty().addListener(ChangeListener { _, _, newValue ->
                        model.name = newValue
                    })
                }

                val validator = ValidationContext().addValidator(textField, textField.textProperty()) {
                    if (it.isNullOrBlank()) error("The internet code field is required") else null
                }
                validator.validate()
                validators.add(validator)
            }

            field("purchase price") {
                val textField = textfield {
                    textProperty().addListener(ChangeListener { _, _, newValue ->
                        model.pp = newValue.toDoubleOrNull()
                    })
                }

                val validator = ValidationContext().addValidator(textField, textField.textProperty()) {
                    val pp = textField.text.toDoubleOrNull()

                    when {
                        pp == null -> error("purchase price is required")
                        pp < 0 -> error("purchase price must be > 0")
                        else -> null
                    }

                }
                validator.validate()
                validators.add(validator)
            }

            field("available") {
                val textField = textfield {
                    textProperty().addListener(ChangeListener { _, _, newValue ->
                        model.available = newValue.toIntOrNull()
                    })
                }

                val validator = ValidationContext().addValidator(textField, textField.textProperty()) {
                    val available = textField.text.toIntOrNull()

                    when {
                        available == null -> error("available is required")
                        available < 0 -> error("available must be > 0")
                        else -> null
                    }

                }
                validator.validate()
                validators.add(validator)
            }
        }

        button("Add") {
            isDefaultButton = true

            action {
                if (!validators.all { it.isValid }) {
                    return@action
                }

                val itemModel = ItemModel().apply {
                    item = transaction(DB.db) {
                        Item.new(model.id) {
                            ic = model.ic!!
                            name = model.name!!
                            pPrice = model.pp!!
                            available = model.available!!
                        }
                    }
                }

                controller.items.add(itemModel)
            }
        }
    }

}

class NewShopForm(val controller: DatabaseController) : Fragment("Adding new Shops") {

    val model = TempShop()
    val validators = LinkedList<ValidationContext.Validator<String>>()


    override val root = form {
        fieldset {
            field("id") {

                val textField = textfield {
                    textProperty().addListener(ChangeListener { _, _, newValue ->
                        model.id = newValue.toIntOrNull()
                    })
                }

                val validator = ValidationContext().addValidator(textField, textField.textProperty()) {
                    val item: Item? = transaction(DB.db) {
                        textField.text.toIntOrNull()?.let { Item.findById(it) }
                    }
                    when {
                        item != null -> {
                            error("There is already an item with this id")
                        }
                        else -> null
                    }

                }

                validators.add(validator)
            }

            field("name") {
                val textField = textfield {
                    textProperty().addListener(ChangeListener { _, _, newValue ->
                        model.name = newValue
                    })
                }

                val validator = ValidationContext().addValidator(textField, textField.textProperty()) {
                    if (it.isNullOrBlank()) error("The name is required") else null
                }
                validator.validate()
                validators.add(validator)
            }

            field("address") {
                textfield {
                    textProperty().addListener(ChangeListener { _, _, newValue ->
                        model.address = newValue
                    })
                }
            }

            field("tax") {
                textfield {
                    textProperty().addListener(ChangeListener { _, _, newValue ->
                        model.tax = newValue
                    })
                }
            }

            field("contact") {
                textfield {
                    textProperty().addListener(ChangeListener { _, _, newValue ->
                        model.contact = newValue
                    })
                }
            }

        }

        button("Add") {
            isDefaultButton = true

            action {
                if (!validators.all { it.isValid }) {
                    return@action
                }

                val shopModel = ShopModel().apply {
                    item = transaction(DB.db) {
                        Shop.new(model.id) {
                            name = model.name!!
                            address = model.address
                            tax = model.tax
                            contact = model.contact
                        }
                    }
                }

                controller.shops.add(shopModel)
            }
        }
    }
}

class NewOrderTable(val controller: DatabaseController, val orders: ObservableList<OrderModel> = FXCollections.observableArrayList()) : Fragment("Order") {

    var netSum = 0.0

    var netSumText: Text by singleAssign()
    var grossSumText: Text by singleAssign()

    val validators = LinkedList<ValidationContext.Validator<String>>()

    fun sumOrders() {
        netSum = orders.sumByDouble { it.netPrice.value * it.quantity.value }
        netSumText.text = "$netSum\n"
        grossSumText.text = "${netSum * 1.27}"
    }

    val model = TempOrder()
    var orderTable: TableViewEditModel<OrderModel> by singleAssign()


    fun tryClosing() {
        if (orderTable.items.all {
                    !it.value.isDirty
                }) {
            close()
        } else {

            confirm(owner = currentWindow, title = "Warning",
                    content = "You have not commited changes in your order.\nIf you click ok, these changes will be lost.",
                    header = "Uncommitted changes!",
                    actionFn = {
                        orderTable.rollback()
                        close()
                    })


        }
    }


    override val root = borderpane {

        top = menubar {
            menu("edit") {
                item("commit") {
                    action {
                        controller.commitDirtyOrders(orderTable.items.asSequence())
                    }
                }
                item("rollback") {
                    action {
                        orderTable.rollback()
                    }
                }
            }
        }

        left = form {
            fieldset {
                field("id") {

                    val textField = textfield {
                        textProperty().addListener(ChangeListener { _, _, newValue ->
                            model.id = newValue.toIntOrNull()
                        })
                    }

                    val validator = ValidationContext().addValidator(textField, textField.textProperty()) {
                        val item: Item? = transaction(DB.db) {
                            textField.text.toIntOrNull()?.let { Item.findById(it) }
                        }
                        when {
                            item != null -> {
                                error("There is already an item with this id")
                            }
                            else -> null
                        }

                    }

                    validators.add(validator)
                }

                field("itemId") {
                    val textField = textfield {
                        textProperty().addListener(ChangeListener { _, _, newValue ->
                            model.itemId = newValue.toIntOrNull()
                        })
                    }

                    val validator = ValidationContext().addValidator(textField, textField.textProperty()) {
                        when {
                            it.isNullOrBlank() -> error("the item id is required")
                            model.itemId == null -> error("not a number")
                            model.itemId?.let { it1 -> transaction(DB.db) { Item.findById(it1) } } == null -> error("there is no item with this id")
                            else -> null
                        }

                    }
                    validator.validate()
                    validators.add(validator)
                }

                field("net price") {
                    val textField = textfield {
                        textProperty().addListener(ChangeListener { _, _, newValue ->
                            model.np = newValue.toDoubleOrNull()
                        })
                    }

                    val validator = ValidationContext().addValidator(textField, textField.textProperty()) {
                        when {
                            it.isNullOrBlank() -> error("net price is required")
                            model.np == null -> error("Not a number")
                            else -> null
                        }

                    }
                    validator.validate()
                    validators.add(validator)
                }

                field("date") {
                    val textField = textfield {
                        textProperty().addListener(ChangeListener { _, _, newValue ->
                            model.date = newValue
                        })
                    }

                    val dateformat = Regex("^((19|2[0-9])[0-9]{2})-(0[1-9]|1[012])-(0[1-9]|[12][0-9]|3[01])\$")

                    val validator = ValidationContext().addValidator(textField, textField.textProperty()) {
                        when {
                            it.isNullOrBlank() -> error("date is required")
                            !it.matches(dateformat) -> error("invalid dateformat")
                            else -> null
                        }
                    }
                    validator.validate()
                    validators.add(validator)
                }

                field("quantity") {
                    val textField = textfield {
                        textProperty().addListener(ChangeListener { _, _, newValue ->
                            model.quantity = newValue.toIntOrNull()
                        })
                    }

                    val validator = ValidationContext().addValidator(textField, textField.textProperty()) {
                        val quantity = textField.text.toIntOrNull()

                        when {
                            quantity == null -> error("quantity is required")
                            quantity < 0 -> error("quantity must be > 0")
                            else -> null
                        }

                    }
                    validator.validate()
                    validators.add(validator)
                }

            }

            button("Add") {
                isDefaultButton = true

                action {
                    if (!validators.all { it.isValid }) {
                        return@action
                    }

                    val orderModel = OrderModel().apply {
                        item = transaction(DB.db) {
                            Order.new(model.id) {
                                itemId = Item[model.itemId!!].id
                                netPrice = model.np!!
                                date = model.date!!
                                quantity = model.quantity!!
                            }
                        }
                    }

                    controller.orders.add(orderModel)
                    orders.add(orderModel)
                }
            }
        }

        center = tableview(orders) {

            column("#", OrderModel::id).fixedWidth(40)
            column("itemId", OrderModel::idOfItem).makeEditable().weightedWidth(0.3)
            readonlyColumn("itemName", OrderModel::itemName).weightedWidth(1)
            val npCol = column("netPrice", OrderModel::netPrice).makeEditable().weightedWidth(0.5)
            val defaultEditCommit = npCol.onEditCommit
            npCol.onEditCommit = EventHandler {
                defaultEditCommit.handle(it)
                sumOrders()
            }
            column("date", OrderModel::date).makeEditable().fixedWidth(90)
            column("quantity", OrderModel::quantity).makeEditable().weightedWidth(0.5)
            selectionModel.selectionMode = SelectionMode.MULTIPLE
            selectOnDrag()
            smartResize()
            enableDirtyTracking()
            orderTable = editModel
        }

        bottom = textflow {
            text("net sum: ")
            netSumText = text()
            text("gross sum: ")
            grossSumText = text()
            sumOrders()
        }


    }
}


class TempItem(var id: Int? = null,
               var ic: String? = null,
               var name: String? = null,
               var pp: Double? = null,
               var available: Int? = null) {}

class TempShop(var id: Int? = null,
               var name: String? = null,
               var address: String? = null,
               var tax: String? = null,
               var contact: String? = null) {}

class TempSale(var id: Int? = null,
               var buyerId: Int? = null,
               var date: String? = null,
               var sp: Double? = null,
               var quantity: Int? = null) {}

class TempOrder(var id: Int? = null,
                var itemId: Int? = null,
                var np: Double? = null,
                var date: String? = null,
                var quantity: Int? = null) {}
