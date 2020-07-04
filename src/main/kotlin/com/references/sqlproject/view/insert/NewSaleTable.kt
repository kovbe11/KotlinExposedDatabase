package com.references.sqlproject.view.insert

import com.references.sqlproject.controller.DatabaseController
import com.references.sqlproject.model.SaleModel
import com.references.sqlproject.view.*
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.event.EventHandler
import javafx.scene.control.TableColumn
import javafx.scene.text.Text
import tornadofx.*
import java.util.*

class NewSaleTable(val controller: DatabaseController, val sales: ObservableList<SaleModel> = FXCollections.observableArrayList()) : Fragment("Sale") {

    var netSumText: Text by singleAssign()
    var grossSumText: Text by singleAssign()

    private val validators = LinkedList<ValidationContext.Validator<String>>()

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

                    val validator1 = idField("ItemID", model::itemId,
                            isRequired = true,
                            mustFind = true,
                            notFoundMsg = "There is no item with this id",
                            finder = controller::findItemById)
                    validator1.validate()
                    validators.add(validator1)

                    val validator2 = idField("BuyerID", model::buyerId,
                            isRequired = true,
                            mustFind = true,
                            notFoundMsg = "There is no shop with this id",
                            finder = controller::findShopById)
                    validator2.validate()
                    validators.add(validator2)

                    val validator3 = priceField("Selling price", model::sp)
                    validators.add(validator3)

                    val validator4 = dateField(model::date)
                    validators.add(validator4)

                    val validator5 = quantityField("Quantity", model::quantity)
                    validators.add(validator5)

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