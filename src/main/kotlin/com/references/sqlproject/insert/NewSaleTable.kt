package com.references.sqlproject.insert

import com.references.sqlproject.DB.db
import com.references.sqlproject.DatabaseController
import com.references.sqlproject.Item
import com.references.sqlproject.Shop
import com.references.sqlproject.models.SaleModel
import com.references.sqlproject.saleview
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.event.EventHandler
import javafx.scene.control.TableColumn
import javafx.scene.text.Text
import org.jetbrains.exposed.sql.transactions.transaction
import tornadofx.*
import java.util.*

class NewSaleTable(val controller: DatabaseController, val sales: ObservableList<SaleModel> = FXCollections.observableArrayList()) : Fragment("Sale") {

    var netSumText: Text by singleAssign()
    var grossSumText: Text by singleAssign()

    val validators = LinkedList<ValidationContext.Validator<String>>()

    fun sumSales() {
        sumElements(netSumText, grossSumText, sales) {
            it.sPrice.value * it.quantity.value
        }
    }

    val model = TempSale()
    var saleTable: TableViewEditModel<SaleModel> by singleAssign()

    fun tryClosing() {
        tryClosing(saleTable, this)
    }

    override val root = borderpane {

        left {
            form {
                fieldset {
//itt biztos lehetne valamit varázsolni a propertykkel hogy ne legyen ugyanaz mint a newordertable feleslegesen de meghaladta a képességeim
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
                                model.itemId?.let { it1 -> transaction(db) { Item.findById(it1) } } == null -> error("there is no item with this id")
                                else -> null
                            }

                        }
                        validator.validate()
                        validators.add(validator)
                    }

                    field("buyer id") {
                        val textField = textfield {
                            textProperty().addListener(ChangeListener { _, _, newValue ->
                                model.buyerId = newValue.toIntOrNull()
                            })
                        }

                        val validator = ValidationContext().addValidator(textField, textField.textProperty()) {
                            when {
                                it.isNullOrBlank() -> error("the item id is required")
                                model.buyerId == null -> error("not a number")
                                model.buyerId?.let { it1 -> transaction(db) { Shop.findById(it1) } } == null -> error("there is no shop with this id")
                                else -> null
                            }

                        }
                        validator.validate()
                        validators.add(validator)
                    }

                    field("selling price") {
                        val textField = textfield {
                            textProperty().addListener(ChangeListener { _, _, newValue ->
                                model.sp = newValue.toDoubleOrNull()
                            })
                        }

                        val validator = ValidationContext().addValidator(textField, textField.textProperty()) {
                            when {
                                it.isNullOrBlank() -> error("net price is required")
                                model.sp == null -> error("Not a number")
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

                        val saleModel = SaleModel().apply {
                            item = controller.insertSale(model)
                        }

                        controller.sales.add(saleModel)
                        sales.add(saleModel)
                        sumSales()
                    }
                }
            }
        }

//itt is lehetne javítani, valahogy örökléssel/függvényekkel biztos meg lehetne ezt csinálni egyszer 2 helyett
        center {
            saleview(sales) {
                @Suppress("UNCHECKED_CAST") val spCol: TableColumn<SaleModel, Double> = columns[6] as TableColumn<SaleModel, Double>
                val defaultEditCommitNp = spCol.onEditCommit
                spCol.onEditCommit = EventHandler {
                    defaultEditCommitNp.handle(it)
                    sumSales()
                }
                @Suppress("UNCHECKED_CAST") val quantityCol: TableColumn<SaleModel, Int> = columns[7] as TableColumn<SaleModel, Int>
                val defaultEditCommitQuantity = quantityCol.onEditCommit
                quantityCol.onEditCommit = EventHandler {
                    defaultEditCommitQuantity.handle(it)
                    sumSales()
                }
                saleTable = editModel
            }
        }

        top {
            commitrollbackbar(controller, saleTable)
        }

        bottom {
            textflow {
                text("net sum: ")
                netSumText = text()
                text("gross sum: ")
                grossSumText = text()
                sumSales()
            }
        }
    }

}