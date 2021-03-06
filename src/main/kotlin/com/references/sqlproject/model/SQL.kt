package com.references.sqlproject.model

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File
import java.sql.Connection

object DB {
    val db by lazy {
        val databasePath: String = File("config.txt").bufferedReader().use {
            it.readLine()
        }
        val temp = Database.connect("jdbc:sqlite:$databasePath", "org.sqlite.JDBC")
        TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE
        temp
    }
}


object Items : IntIdTable() {
    val ic = text("internet_code")
    val name = text("name")
    val purchasePrice = double("purchase_price")
    val available = integer("available")
}

class Item(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Item>(Items)

    var ic by Items.ic
    var name by Items.name
    var pPrice by Items.purchasePrice
    var available by Items.available

    override fun toString(): String {
        return name
    }
}


object Shops : IntIdTable() {
    val name = text("name")
    val address = text("address").nullable()
    val tax = text("tax").nullable()
    val contact = text("contact").nullable()
}

class Shop(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Shop>(Shops)

    var name by Shops.name
    var address by Shops.address
    var tax by Shops.tax
    var contact by Shops.contact
    val lastSaleDate: String
        get() {
            val ret = transaction(DB.db) {
                (Sales leftJoin Shops).select {
                    Sales.buyerId eq this@Shop.id
                }.adjustSlice {
                    this.slice(Sales.date)
                }.maxBy { it[Sales.date] }
            }
            return if (ret == null) {
                "-"
            } else {
                ret[Sales.date]
            }
        }

    override fun toString(): String {
        return name
    }
}


object Orders : IntIdTable() {
    val itemId = reference("item_id", Items.id)
    val netPrice = double("net_price")
    val date = text("date").check { it.like("____-__-__") }
    val quantity = integer("quantity")
}

class Order(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Order>(Orders)

    var itemId by Orders.itemId
    var itemIdInt: Int
        get() {
            return itemId.value
        }
        set(value) {
            transaction(db) {
                itemId = Item[value].id
            }
        }

    var netPrice by Orders.netPrice
    var date by Orders.date
    var quantity by Orders.quantity

    override fun toString(): String {
        return "$id"
    }
}

object Sales : IntIdTable() {
    val itemId = reference("item_id", Items.id)
    val buyerId = reference("buyer_id", Shops.id)
    val date = text("date").check { it.like("____-__-__") }
    val sellingPrice = double("selling_price").check { it.greaterEq(0.0) }
    val quantity = integer("quantity")
}

class Sale(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Sale>(Sales)

    var itemId by Sales.itemId
    var itemIdInt: Int
        get() {
            return itemId.value
        }
        set(value) {
            transaction(db) {
                itemId = Item[value].id
            }
        }

    var buyerId by Sales.buyerId
    var buyerIdInt: Int
        get() {
            return buyerId.value
        }
        set(value) {
            transaction(db) {
                buyerId = Shop[value].id
            }
        }


    var date by Sales.date
    var sPrice by Sales.sellingPrice
    var quantity by Sales.quantity

    override fun toString(): String {
        return "$id"
    }

}