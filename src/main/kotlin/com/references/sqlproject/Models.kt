package com.references.sqlproject

import org.jetbrains.exposed.sql.transactions.transaction
import tornadofx.ItemViewModel


//tornadofx modellek
class ItemModel : ItemViewModel<Item>() {
    val id = bind(Item::id)
    val ic = bind(Item::ic)
    val name = bind(Item::name)
    val pPrice = bind(Item::pPrice)
    val available = bind(Item::available)
}

class ShopModel : ItemViewModel<Shop>() {
    val id = bind(Shop::id)
    val name = bind(Shop::name)
    val address = bind(Shop::address)
    val tax = bind(Shop::tax)
    val contact = bind(Shop::contact)
    val lastSaleDate = bind(Shop::lastSaleDate)
}

class OrderModel : ItemViewModel<Order>() {
    val id = bind(Order::id)
    val itemId = bind(Order::itemId)
    val itemName: String
        get() {
            return transaction(DB.db) {
                Item[itemId.value].name
            }
        }
    val netPrice = bind(Order::netPrice)
    val date = bind(Order::netPrice)
    val quantity = bind(Order::quantity)
}

class SaleModel : ItemViewModel<Sale>() {
    val id = bind(Sale::id)
    val itemId = bind(Sale::itemId)
    val itemName: String
        get() {
            return transaction(DB.db) {
                Item[itemId.value].name
            }
        }
    val buyerId = bind(Sale::buyerId)
    val shopName: String
        get() {

            return transaction(DB.db) {
                Shop[buyerId.value].name
            }
        }
    val date = bind(Sale::date)
    val sPrice = bind(Sale::sPrice)
    val quantity = bind(Sale::quantity)
}