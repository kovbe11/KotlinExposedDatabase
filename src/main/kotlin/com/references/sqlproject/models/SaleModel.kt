package com.references.sqlproject.models

import com.references.sqlproject.DB
import com.references.sqlproject.Item
import com.references.sqlproject.Sale
import com.references.sqlproject.Shop
import org.jetbrains.exposed.sql.transactions.transaction

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