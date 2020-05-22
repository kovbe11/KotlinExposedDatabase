package com.references.sqlproject.view.insert

import com.references.sqlproject.controller.DatabaseController
import com.references.sqlproject.model.DB
import com.references.sqlproject.model.Item
import com.references.sqlproject.model.OrderModel
import com.references.sqlproject.view.orderview
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.event.EventHandler
import javafx.scene.control.TableColumn
import javafx.scene.text.Text
import org.jetbrains.exposed.sql.transactions.transaction
import tornadofx.*
import java.util.*

class NewOrderTable(val controller: DatabaseController,
                    val orders: ObservableList<OrderModel> = FXCollections.observableArrayList()) :
        Fragment("Order") {

    var netSumText: Text by singleAssign()
    var grossSumText: Text by singleAssign()

    val validators = LinkedList<ValidationContext.Validator<String>>()

    fun sumOrders() {
        sumElements(netSumText, grossSumText, orders) {
            it.netPrice.value * it.quantity.value
        }
    }

    val model = TempOrder()
    var orderTable: TableViewEditModel<OrderModel> by singleAssign()


    fun tryClosing() {
        tryClosing(orderTable, this)
    }


    override val root = borderpane {


        left {
            form {
                fieldset {
//itt biztos lehetne valamit varázsolni a propertykkel de meghaladta a képességeim
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
                            item = controller.insertOrder(model)
                        }

                        controller.orders.add(orderModel)
                        orders.add(orderModel)
                        sumOrders()
                    }
                }
            }
        }

        center {
            orderview(orders) {
                @Suppress("UNCHECKED_CAST") val npCol: TableColumn<OrderModel, Double> = columns[3] as TableColumn<OrderModel, Double>
                val defaultEditCommitNp = npCol.onEditCommit
                npCol.onEditCommit = EventHandler {
                    defaultEditCommitNp.handle(it)
                    sumOrders()
                }
                @Suppress("UNCHECKED_CAST") val quantityCol: TableColumn<OrderModel, Int> = columns[5] as TableColumn<OrderModel, Int>
                val defaultEditCommitQuantity = quantityCol.onEditCommit
                quantityCol.onEditCommit = EventHandler {
                    defaultEditCommitQuantity.handle(it)
                    sumOrders()
                }
                orderTable = editModel
            }
        }

        top {
            commitrollbackbar(controller, orderTable)
        }


        bottom {
            textflow {
                text("net sum: ")
                netSumText = text()
                text("gross sum: ")
                grossSumText = text()
                sumOrders()
            }
        }
    }


}
