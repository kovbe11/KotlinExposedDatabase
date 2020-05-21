package com.references.sqlproject

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.sql.transactions.transaction
import tornadofx.ItemViewModel


//tornadofx modellek

//egy szégyenteljes próbálkozás a generikus osztályok összeszervezésére
abstract class Model<T : IntEntity> : ItemViewModel<T>() {
    abstract fun contains(subString: String): Boolean
}

class ItemModel : Model<Item>() {
    val id = bind(Item::id)
    val ic = bind(Item::ic)
    val name = bind(Item::name)
    val pPrice = bind(Item::pPrice)
    val available = bind(Item::available)
    override fun contains(subString: String): Boolean {
        return id.value.value.toString().contentEquals(subString) ||
                name.value.contains(subString) ||
                ic.value.contains(subString) ||
                pPrice.value.toString().contentEquals(subString) ||
                available.value.toString().contentEquals(subString)
    }

    override fun toString(): String {
        return item.name
    }
}

class ShopModel : Model<Shop>() {
    val id = bind(Shop::id)
    val name = bind(Shop::name)
    val address = bind(Shop::address)
    val tax = bind(Shop::tax)
    val contact = bind(Shop::contact)
    val lastSaleDate = bind(Shop::lastSaleDate)
    override fun contains(subString: String): Boolean {
        return id.value.value.toString().contentEquals(subString) ||
                name.value.contains(subString) ||
                if (address.value == null) {
                    false
                } else {
                    address.value.contains(subString)
                } ||
                if (tax.value == null) {
                    false
                } else {
                    tax.value.contains(subString)
                } ||
                if (contact.value == null) {
                    false
                } else {
                    contact.value.contains(subString)
                } ||
                lastSaleDate.value.contains(subString)
    }

    override fun toString(): String {
        return item.name
    }
}

class OrderModel : Model<Order>() {
    val id = bind(Order::id)
    val idOfItem = bind(Order::itemIdInt)

    //TODO: ez nem ide való hanem az SQLbe.
    val itemName: String
        get() {
            return transaction(DB.db) {
                Item[idOfItem.value].name
            }
        }
    val netPrice = bind(Order::netPrice)
    val date = bind(Order::date)
    val quantity = bind(Order::quantity)
    override fun contains(subString: String): Boolean {
        return id.value.value.toString().contentEquals(subString) ||
                idOfItem.value.toString().contentEquals(subString) ||
                itemName.contains(subString) ||
                netPrice.value.toString().contentEquals(subString) ||
                date.value.contains(subString) ||
                quantity.value.toString().contentEquals(subString)
    }

    override fun toString(): String {
        return item.id.value.toString()
    }
}

class SaleModel : Model<Sale>() {
    val id = bind(Sale::id)
    val itemId = bind(Sale::itemIdInt)
    val itemName: String
        get() {
            return transaction(DB.db) {
                Item[itemId.value].name
            }
        }
    val buyerId = bind(Sale::buyerIdInt)
    val shopName: String
        get() {

            return transaction(DB.db) {
                Shop[buyerId.value].name
            }
        }
    val date = bind(Sale::date)
    val sPrice = bind(Sale::sPrice)
    val quantity = bind(Sale::quantity)


    override fun contains(subString: String): Boolean {
        return id.value.value.toString().contentEquals(subString) ||
                itemId.value.toString().contentEquals(subString) ||
                itemName.contains(subString) ||
                buyerId.value.toString().contentEquals(subString) ||
                shopName.contains(subString) ||
                date.value.contains(subString) ||
                sPrice.value.toString().contentEquals(subString) ||
                quantity.value.toString().contentEquals(subString)
    }

    override fun toString(): String {
        return item.id.value.toString()
    }
}