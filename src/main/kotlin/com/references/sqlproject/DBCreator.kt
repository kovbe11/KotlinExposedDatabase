package com.references.sqlproject

import com.references.sqlproject.model.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File
import java.sql.Connection
import kotlin.random.Random

fun main() {
    val file = File("testdb.db")
    if (!file.exists()) {
        file.createNewFile()
    }
    val testDB = Database.connect("jdbc:sqlite:${file.path}", "org.sqlite.JDBC")
    TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE

    val itemCount = 1000
    val shopCount = 100
    val orderCount = 5000
    val saleCount = 4000

    transaction(testDB) {
        addLogger(StdOutSqlLogger)
        SchemaUtils.create(Items, Shops, Orders, Sales)
        for (i in 1..itemCount) {
            Item.new {
                ic = "ic$i"
                name = "item$i"
                pPrice = Random.nextDouble(5000.0)
                available = Random.nextInt(100)
            }
        }
        commit()
        for (i in 1..shopCount) {
            Shop.new {
                name = "shop$i"
                if (Random.nextBoolean()) {
                    address = "address$i"
                }
                if (Random.nextBoolean()) {
                    tax = "tax$i"
                }
                if (Random.nextBoolean()) {
                    contact = "contact$i"
                }
            }
        }
        commit()

        for (i in 1..orderCount) {
            Order.new {
                itemId = Item.findById(Random.nextInt(itemCount - 1) + 1)!!.id
                netPrice = Item.findById(itemId)!!.pPrice
                val rand = Random.nextInt(4)
                date = when (rand) {
                    0 -> "2020-04-21"
                    1 -> "2020-03-11"
                    2 -> "2020-04-29"
                    3 -> "2020-04-19"
                    else -> "2020-01-01" // kell neki az else ág
                }
                quantity = Random.nextInt(200)
            }
        }
        commit()

        for (i in 1..saleCount) {
            Sale.new {
                itemId = Item.findById(Random.nextInt(itemCount - 1) + 1)!!.id
                buyerId = Shop.findById(Random.nextInt(shopCount - 1) + 1)!!.id
                sPrice = Item.findById(itemId)!!.pPrice
                val rand = Random.nextInt(4)
                date = when (rand) {
                    0 -> "2020-05-21"
                    1 -> "2020-05-11"
                    2 -> "2020-04-29"
                    3 -> "2020-05-19"
                    else -> "2020-01-01"
                }
                quantity = Random.nextInt(200)
            }
        }
        commit()

    }


}