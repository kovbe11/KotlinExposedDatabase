package com.references.sqlproject.model

import com.references.sqlproject.controller.DatabaseController

class OrderModel : Model<Order>() {

    val id = bind(Order::id)
    val idOfItem = bind(Order::itemIdInt)

    val controller: DatabaseController by lazy {
        find(DatabaseController::class)
    }

    val itemName: String
        get() {
            return controller.item[idOfItem.value] as String
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