package com.references.sqlproject.view.insert

import com.references.sqlproject.controller.DatabaseController
import com.references.sqlproject.model.OrderModel
import com.references.sqlproject.view.*
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.event.EventHandler
import javafx.scene.control.TableColumn
import javafx.scene.text.Text
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

                    val validator1 = idField("ItemID", model::itemId,
                            isRequired = true,
                            mustFind = true,
                            notFoundMsg = "There is no item with this id",
                            finder = controller::findItemById)
                    validator1.validate()
                    validators.add(validator1)

                    val validator2 = priceField("Net price", model::np)
                    validators.add(validator2)

                    val validator3 = dateField(model::date)
                    validators.add(validator3)

                    val validator4 = quantityField("Quantity", model::quantity)
                    validators.add(validator4)
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
