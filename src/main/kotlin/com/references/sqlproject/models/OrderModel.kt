package com.references.sqlproject.models

import com.references.sqlproject.DB
import com.references.sqlproject.Item
import com.references.sqlproject.Order
import org.jetbrains.exposed.sql.transactions.transaction

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