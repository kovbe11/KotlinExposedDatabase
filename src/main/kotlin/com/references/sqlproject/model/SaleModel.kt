package com.references.sqlproject.model

import com.references.sqlproject.controller.DatabaseController

class SaleModel : Model<Sale>() {
    val id = bind(Sale::id)
    val itemId = bind(Sale::itemIdInt)

    val controller: DatabaseController by lazy {
        find(DatabaseController::class)
    }

    val itemName: String
        get() {
            return controller.item[itemId.value] as String
        }

    val buyerId = bind(Sale::buyerIdInt)


    val shopName: String
        get() {
            return controller.shop[buyerId.value] as String
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