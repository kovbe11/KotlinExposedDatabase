package com.references.sqlproject

import com.references.sqlproject.insert.NewItemForm
import com.references.sqlproject.insert.NewOrderTable
import com.references.sqlproject.insert.NewSaleTable
import com.references.sqlproject.insert.NewShopForm
import com.references.sqlproject.models.ItemModel
import com.references.sqlproject.models.OrderModel
import com.references.sqlproject.models.SaleModel
import com.references.sqlproject.models.ShopModel
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.StringProperty
import javafx.event.EventHandler
import javafx.scene.control.TabPane
import javafx.scene.control.TableView
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.layout.Priority
import javafx.stage.Modality
import tornadofx.*
import java.util.*


fun deleteHandle(table: TableView<*>, controller: DatabaseController, event: KeyEvent) {
    if (event.code == KeyCode.DELETE) {
        val selected = table.selectionModel.selectedCells
        val msg = selected.joinToString(limit = 3, truncated = "\nand ${selected.size - 3} more") {
            table.items[it.row].toString()
        }

        confirm("Are you sure you want to delete this item?", "Selected item is: $msg", actionFn = {
            table.selectionModel.selectedItems.toSet().forEach {
                controller.delete(it)
            }
        })
        event.consume()
    }
}

class MainView : View("Database") {

    private val controller: DatabaseController by inject()

    private var itemTable: TableViewEditModel<ItemModel> by singleAssign()
    private var shopTable: TableViewEditModel<ShopModel> by singleAssign()
    private var orderTable: TableViewEditModel<OrderModel> by singleAssign()
    private var saleTable: TableViewEditModel<SaleModel> by singleAssign()

    var filter: StringProperty by singleAssign()
    val filterTarget: SimpleObjectProperty<FilterTargetType> = SimpleObjectProperty(FilterTargetType.All)

    val items = SortedFilteredList(controller.items)
    val shops = SortedFilteredList(controller.shops)
    val orders = SortedFilteredList(controller.orders)
    val sales = SortedFilteredList(controller.sales)

    private val errors: LinkedList<String> = LinkedList()

    fun registerError(msg: String) {
        errors.add(msg)
    }

    fun showErrors() {
        val errorMsg: String = errors.joinToString(limit = 3, truncated = "\nand ${errors.size - 3} more")
        errors.clear()
        error("Something went wrong!", content = errorMsg)
    }

    override val root = vbox {
        menubar {
            menu("Edit") {
                menu("New..") {
                    item("Item") {
                        action {
                            newItem()
                        }
                    }
                    item("Shop") {
                        action {
                            newShop()
                        }
                    }
                    item("Order") {
                        action {
                            newOrder()
                        }
                    }
                    item("Sale") {
                        action {
                            newSale()
                        }
                    }
                }
                menu("Commit..") {
                    item("Items") {
                        action {
                            controller.commitDirty(itemTable.items)
                        }
                    }
                    item("Shops") {
                        action {
                            controller.commitDirty(shopTable.items)
                        }
                    }
                    item("Orders") {
                        action {
                            controller.commitDirty(orderTable.items)
                        }
                    }
                    item("Sales") {
                        action {
                            controller.commitDirty(saleTable.items)
                        }
                    }
                }
                item("Rollback") {
                    action {
                        itemTable.rollback()
                        shopTable.rollback()
                        orderTable.rollback()
                        saleTable.rollback()
                    }
                }
            }
        }
        hbox {
            textfield {
                hgrow = Priority.ALWAYS
                isFocusTraversable = false
                filter = textProperty()
            }
            combobox(filterTarget, FilterTargetType.values().asList().asObservable()) {
                isFocusTraversable = false
            }
        }

        splitpane {
            vgrow = Priority.ALWAYS
            setDividerPosition(0, 0.5)
            draggabletabpane {
                isFocusTraversable = false

                draggabletab("Items") {
                    isFocusTraversable = false
                    tabClosingPolicy = TabPane.TabClosingPolicy.UNAVAILABLE

                    itemview(this@MainView.items) {
                        val filterer = items as SortedFilteredList
                        filterer.filterWhen(filter) { filter, item ->
                            item.contains(filter) || !filterTarget.value.isEnabled(FilterTargetType.Items)
                        }

                        filterer.filterWhen(filterTarget) { _, item ->
                            item.contains(filter.get()) || !filterTarget.value.isEnabled(FilterTargetType.Items)
                        }
                        itemTable = editModel

                        onKeyPressed = EventHandler {
                            deleteHandle(this, controller, it)
                        }
                    }

                }
                draggabletab("Shops") {
                    tabClosingPolicy = TabPane.TabClosingPolicy.UNAVAILABLE
                    isFocusTraversable = false

                    shopview(shops) {
                        //TODO: ez a filteres cucc egy if(items is SortedFilteredList) el betehető egy TableView extensionként!
                        val filterer = items as SortedFilteredList

                        filterer.filterWhen(filter) { filter, item ->
                            item.contains(filter) || !filterTarget.value.isEnabled(FilterTargetType.Shops)
                        }

                        filterer.filterWhen(filterTarget) { _, item ->
                            item.contains(filter.get()) || !filterTarget.value.isEnabled(FilterTargetType.Shops)
                        }
                        shopTable = editModel

                        onKeyPressed = EventHandler {
                            deleteHandle(this, controller, it)
                        }

                    }
                }
            }
            draggabletabpane {
                isFocusTraversable = false

                draggabletab("Orders") {
                    tabClosingPolicy = TabPane.TabClosingPolicy.UNAVAILABLE
                    isFocusTraversable = false

                    orderview(orders) {

                        val filterer = items as SortedFilteredList
                        filterer.filterWhen(filter) { filter, item ->
                            item.contains(filter) || !filterTarget.value.isEnabled(FilterTargetType.Orders)
                        }

                        filterer.filterWhen(filterTarget) { _, item ->
                            item.contains(filter.get()) || !filterTarget.value.isEnabled(FilterTargetType.Orders)
                        }
                        orderTable = editModel


                        onKeyPressed = EventHandler {
                            deleteHandle(this, controller, it)
                        }
                    }
                }
                draggabletab("Sales") {
                    tabClosingPolicy = TabPane.TabClosingPolicy.UNAVAILABLE
                    isFocusTraversable = false

                    saleview(sales) {

                        val filterer = items as SortedFilteredList
                        filterer.filterWhen(filter) { filter, item ->
                            item.contains(filter) || !filterTarget.value.isEnabled(FilterTargetType.Sales)
                        }

                        filterer.filterWhen(filterTarget) { _, item ->
                            item.contains(filter.get()) || !filterTarget.value.isEnabled(FilterTargetType.Sales)
                        }
                        saleTable = editModel

                        onKeyPressed = EventHandler {
                            deleteHandle(this, controller, it)
                        }
                    }
                }
            }
        }
    }

    private fun newItem() {
        NewItemForm(controller).openModal(modality = Modality.NONE)
    }

    private fun newShop() {
        NewShopForm(controller).openModal(modality = Modality.NONE)
    }

    private fun newOrder() {

        val newOrderTable =
                if (orderTable.tableView.selectionModel.selectedItems.isEmpty()) NewOrderTable(controller)
                else NewOrderTable(controller, orderTable.tableView.selectionModel.selectedItems)

        newOrderTable.openModal(modality = Modality.NONE)?.setOnCloseRequest {
            newOrderTable.tryClosing()
            it.consume()
        }
    }

    private fun newSale() {
        val newSaleTable =
                if (saleTable.tableView.selectionModel.selectedItems.isEmpty()) NewSaleTable(controller)
                else NewSaleTable(controller, saleTable.tableView.selectionModel.selectedItems)

        newSaleTable.openModal(modality = Modality.NONE)?.setOnCloseRequest {
            newSaleTable.tryClosing()
            it.consume()
        }
    }

}
