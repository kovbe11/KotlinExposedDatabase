package com.references.sqlproject

import javafx.scene.control.TabPane
import tornadofx.*

class DatabaseView : View("Database") {

    private val controller: DatabaseController by inject()


    private var itemTable: TableViewEditModel<ItemModel> by singleAssign()
    private var shopTable: TableViewEditModel<ShopModel> by singleAssign()
    private var orderTable: TableViewEditModel<OrderModel> by singleAssign()
    private var saleTable: TableViewEditModel<SaleModel> by singleAssign()

    override val root = vbox {
        menubar {
            menu("File") {
                menu("New..") {
                    item("Item")
                    item("Shop")
                    item("Order")
                    item("Sale")
                }
                item("export to csv")
            }
            menu("Edit"){
                menu("Commit.."){
                    item("Items"){
                        action {
                            controller.commitDirtyItems(itemTable.items.asSequence())
                        }
                    }
                    item("Shops"){
                        action{
                            controller.commitDirtyShops(shopTable.items.asSequence())
                        }
                    }
                    item("Orders"){
                        action{
                            controller.commitDirtyOrders(orderTable.items.asSequence())
                        }
                    }
                    item("Sales"){
                        action{
                            controller.commitDirtySales(saleTable.items.asSequence())
                        }
                    }
                }
                item("Rollback"){
                    action{
                        itemTable.rollback()
                        shopTable.rollback()
                        orderTable.rollback()
                        saleTable.rollback()
                    }
                }
            }
        }
        splitpane {
            setDividerPosition(0, 0.5)
            tabpane {
                tab("Items") {
                    tabClosingPolicy = TabPane.TabClosingPolicy.UNAVAILABLE

                    tableview<ItemModel> {
                        items = controller.items
                        itemTable = editModel


                        column("id", ItemModel::id)
                        column("ic", ItemModel::ic)
                        column("name", ItemModel::name)
                        column("purchase price", ItemModel::pPrice)
                        column("available", ItemModel::available)
                    }
                }
                tab("Shops") {
                    tabClosingPolicy = TabPane.TabClosingPolicy.UNAVAILABLE

                    tableview<ShopModel> {
                        items = controller.shops
                        shopTable = editModel


                        column("id", ShopModel::id)
                        column("name", ShopModel::name)
                        column("address", ShopModel::address)
                        column("tax", ShopModel::tax)
                        column("contact", ShopModel::contact)
                        column("last sale date", ShopModel::lastSaleDate)
                    }
                }
            }
            tabpane {
                tab("Orders") {
                    tabClosingPolicy = TabPane.TabClosingPolicy.UNAVAILABLE

                    tableview<OrderModel> {
                        items = controller.orders
                        orderTable = editModel


                        column("id", OrderModel::id)
                        column("itemId", OrderModel::itemId)
                        readonlyColumn("itemName", OrderModel::itemName)
                        column("netPrice", OrderModel::netPrice)
                        column("date", OrderModel::date)
                        column("quantity", OrderModel::quantity)
                    }
                }
                tab("Sales") {
                    tabClosingPolicy = TabPane.TabClosingPolicy.UNAVAILABLE

                    tableview<SaleModel> {
                        items = controller.sales
                        saleTable = editModel


                        column("id", SaleModel::id)
                        column("itemId", SaleModel::itemId)
                        readonlyColumn("itemName", SaleModel::itemName)
                        column("buyerId", SaleModel::buyerId)
                        readonlyColumn("shop name", SaleModel::shopName)
                        column("date", SaleModel::date)
                        column("selling price", SaleModel::sPrice)
                        column("quantity", SaleModel::quantity)
                    }
                }
            }
        }
    }
}
