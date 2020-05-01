package com.references.sqlproject

import javafx.collections.ObservableList
import org.jetbrains.exposed.sql.transactions.transaction
import tornadofx.Controller
import tornadofx.asObservable

class DatabaseController : Controller() {

    val items: ObservableList<ItemModel> by lazy {
        transaction(DB.db) {
            Item.all().map {
                ItemModel().apply {
                    item = it
                }
            }.asObservable()
        }
    }

    val shops: ObservableList<ShopModel> by lazy {
        transaction(DB.db) {
            Shop.all().map {
                ShopModel().apply {
                    item = it
                }
            }.asObservable()
        }
    }

    val orders: ObservableList<OrderModel> by lazy{
        transaction(DB.db) {
            Order.all().map {
                OrderModel().apply {
                    item = it
                }
            }.asObservable()
        }
    }

    val sales: ObservableList<SaleModel> by lazy {
        transaction(DB.db) {
            Sale.all().map {
                SaleModel().apply {
                    item = it
                }
            }.asObservable()
        }
    }


}