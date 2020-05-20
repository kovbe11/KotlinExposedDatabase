package com.references.sqlproject

import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.StringProperty
import javafx.event.EventHandler
import javafx.scene.control.SelectionMode
import javafx.scene.control.TabPane
import javafx.scene.input.KeyCode
import javafx.scene.layout.Priority
import tornadofx.*

class MainView : View("Database") {

    private val controller: DatabaseController by inject()

    var itemTable: TableViewEditModel<ItemModel> by singleAssign()
    var shopTable: TableViewEditModel<ShopModel> by singleAssign()
    private var orderTable: TableViewEditModel<OrderModel> by singleAssign()
    private var saleTable: TableViewEditModel<SaleModel> by singleAssign()

    var filter: StringProperty by singleAssign()
    val filterTarget: SimpleObjectProperty<FilterTargetType> = SimpleObjectProperty(FilterTargetType.All)

    val items = SortedFilteredList(controller.items)
    val shops = SortedFilteredList(controller.shops)
    val orders = SortedFilteredList(controller.orders)
    val sales = SortedFilteredList(controller.sales)


    override val root = vbox {
        menubar {
            menu("File") {
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
            }
            menu("Edit") {
                menu("Commit..") {
                    item("Items") {
                        action {
                            controller.commitDirtyItems(itemTable.items.asSequence())
                        }
                    }
                    item("Shops") {
                        action {
                            controller.commitDirtyShops(shopTable.items.asSequence())
                        }
                    }
                    item("Orders") {
                        action {
                            controller.commitDirtyOrders(orderTable.items.asSequence())
                        }
                    }
                    item("Sales") {
                        action {
                            controller.commitDirtySales(saleTable.items.asSequence())
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

                    tableview<ItemModel> {

                        column("#", ItemModel::id).fixedWidth(40)
                        column("ic", ItemModel::ic).makeEditable().weightedWidth(1.0)
                        column("name", ItemModel::name).makeEditable().remainingWidth()
                        column("purchase price", ItemModel::pPrice).makeEditable().weightedWidth(1.0)
                        column("available", ItemModel::available).makeEditable().weightedWidth(1.0)

                        val filterer = this@MainView.items.bindTo(this)


                        filterer.filterWhen(filter) { filter, item ->
                            item.contains(filter) || !filterTarget.value.isEnabled(FilterTargetType.Items)
                        }

                        filterer.filterWhen(filterTarget) { _, item ->
                            item.contains(filter.get()) || !filterTarget.value.isEnabled(FilterTargetType.Items)
                        }


                        smartResize()

                        itemTable = editModel
                        enableDirtyTracking()
                        selectionModel.selectionMode = SelectionMode.MULTIPLE
                        selectOnDrag()

                        //TODO: miért nem tudom ezt duplikáció nélkül kihozni a többire is??
                        onKeyPressed = EventHandler {
                            if (it.code == KeyCode.DELETE) {
                                val selected = selectionModel.selectedCells
                                var msg = selected.joinToString(limit = 3, truncated = "\nand ${selected.size - 3} more") {
                                    items[it.row].name.value
                                }

                                confirm("Are you sure you want to delete this item?", "Selected item is: $msg", actionFn = {
                                    selectionModel.selectedItems.forEach {
                                        controller.delete(it)
                                    }
                                })
                            }
                        }


                    }
                }
                draggabletab("Shops") {
                    tabClosingPolicy = TabPane.TabClosingPolicy.UNAVAILABLE
                    isFocusTraversable = false

                    tableview<ShopModel> {

                        column("#", ShopModel::id).fixedWidth(40)
                        column("name", ShopModel::name).makeEditable().remainingWidth()
                        column("address", ShopModel::address).makeEditable().weightedWidth(0.5)
                        column("tax", ShopModel::tax).makeEditable().weightedWidth(0.5)
                        column("contact", ShopModel::contact).makeEditable().weightedWidth(0.5)
                        column("last sale", ShopModel::lastSaleDate).fixedWidth(90)

                        selectOnDrag()

                        val filterer = shops.bindTo(this)
                        filterer.filterWhen(filter) { filter, item ->
                            item.contains(filter) || !filterTarget.value.isEnabled(FilterTargetType.Shops)
                        }

                        filterer.filterWhen(filterTarget) { _, item ->
                            item.contains(filter.get()) || !filterTarget.value.isEnabled(FilterTargetType.Shops)
                        }
                        shopTable = editModel

                        enableDirtyTracking()
                        selectionModel.selectionMode = SelectionMode.MULTIPLE


                        smartResize()
                    }
                }
            }
            draggabletabpane {
                isFocusTraversable = false

                draggabletab("Orders") {
                    tabClosingPolicy = TabPane.TabClosingPolicy.UNAVAILABLE
                    isFocusTraversable = false

                    tableview<OrderModel> {

                        column("#", OrderModel::id).fixedWidth(40)
                        column("itemId", OrderModel::idOfItem).makeEditable().weightedWidth(0.3)
                        readonlyColumn("itemName", OrderModel::itemName).weightedWidth(1)
                        column("netPrice", OrderModel::netPrice).makeEditable().weightedWidth(0.5)
                        column("date", OrderModel::date).makeEditable().fixedWidth(90)
                        column("quantity", OrderModel::quantity).makeEditable().weightedWidth(0.5)
                        selectOnDrag()


                        val filterer = orders.bindTo(this)
                        filterer.filterWhen(filter) { filter, item ->
                            item.contains(filter) || !filterTarget.value.isEnabled(FilterTargetType.Orders)
                        }

                        filterer.filterWhen(filterTarget) { _, item ->
                            item.contains(filter.get()) || !filterTarget.value.isEnabled(FilterTargetType.Orders)
                        }
                        orderTable = editModel


                        enableDirtyTracking()

                        selectionModel.selectionMode = SelectionMode.MULTIPLE

                        smartResize()
                    }
                }
                draggabletab("Sales") {
                    tabClosingPolicy = TabPane.TabClosingPolicy.UNAVAILABLE
                    isFocusTraversable = false

                    tableview<SaleModel> {

                        column("#", SaleModel::id).fixedWidth(40)
                        column("itemId", SaleModel::itemId).makeEditable().weightedWidth(0.3)
                        readonlyColumn("itemName", SaleModel::itemName).weightedWidth(1.0)
                        column("buyerId", SaleModel::buyerId).makeEditable().weightedWidth(0.3)
                        readonlyColumn("shop name", SaleModel::shopName).weightedWidth(1.0)
                        column("date", SaleModel::date).makeEditable()
                        column("selling price", SaleModel::sPrice).makeEditable().weightedWidth(0.5)
                        column("quantity", SaleModel::quantity).makeEditable().weightedWidth(0.5)

                        selectOnDrag()

                        val filterer = sales.bindTo(this)
                        filterer.filterWhen(filter) { filter, item ->
                            item.contains(filter) || !filterTarget.value.isEnabled(FilterTargetType.Sales)
                        }

                        filterer.filterWhen(filterTarget) { _, item ->
                            item.contains(filter.get()) || !filterTarget.value.isEnabled(FilterTargetType.Sales)
                        }
                        saleTable = editModel

                        enableDirtyTracking()

                        selectionModel.selectionMode = SelectionMode.MULTIPLE

                        smartResize()
                    }
                }
            }
        }
    }

    fun newItem() {
        NewItemForm(controller).openModal()
    }

    fun newShop() {
        NewShopForm(controller).openModal()
    }

    fun newOrder() {
        val newOrderTable = NewOrderTable(controller, orderTable.tableView.selectionModel.selectedItems)
        newOrderTable.openModal()?.setOnCloseRequest {
            newOrderTable.tryClosing()
            it.consume()
        }
    }

    fun newSale() {
        dialog("new Sales") {

            form {

            }


            button("Add") {
                isDefaultButton = true
            }
        }

    }

}
