package com.references.sqlproject

import com.references.sqlproject.DB.db
import javafx.collections.ObservableList
import org.jetbrains.exposed.sql.transactions.transaction
import tornadofx.Controller
import tornadofx.ItemViewModel
import tornadofx.TableColumnDirtyState
import tornadofx.asObservable


//minden ami adatbázissal kommunikál az alkalmazásból az ide kerül

class DatabaseController : Controller() {

    val items: ObservableList<ItemModel> by lazy {
        transaction(db) {
            Item.all().map {
                ItemModel().apply {
                    item = it
                }
            }.asObservable()
        }
    }

    val shops: ObservableList<ShopModel> by lazy {
        transaction(db) {
            Shop.all().map {
                ShopModel().apply {
                    item = it
                }
            }.asObservable()
        }
    }

    val orders: ObservableList<OrderModel> by lazy {
        transaction(db) {
            Order.all().map {
                OrderModel().apply {
                    item = it
                }
            }.asObservable()
        }
    }

    val sales: ObservableList<SaleModel> by lazy {
        transaction(db) {
            Sale.all().map {
                SaleModel().apply {
                    item = it
                }
            }.asObservable()
        }
    }

    fun delete(model: Any) {
        when (model) {
            is ItemModel -> delete(model)
            is ShopModel -> delete(model)
            is OrderModel -> delete(model)
            is SaleModel -> delete(model)
            else -> throw IllegalArgumentException()
        }
    }


    private fun delete(model: ItemModel) {

        //SQLITE nem szereti az exposedot, alapból ki van kapcsolva az fk constraint
        //és van egy issue a githubon ami ezt tárgyalja, és sajnos nincs rá megoldás
        //szóval ellenőrzök inkább kézzel.

        transaction(db) {
            if (!Order.find { Orders.itemId eq model.item.id }.empty()) {
                throw ForeignKeyViolation("There is an order referencing this item.")
            }
            if (!Sale.find { Sales.itemId eq model.item.id }.empty()) {
                throw ForeignKeyViolation("There is a sale referencing this item.")
            }
        }

        transaction(db) {
            model.item.delete()
        }
        items.remove(model)
    }

    private fun delete(model: SaleModel) {
        transaction(db) {
            model.item.delete()
        }
        sales.remove(model)
    }

    private fun delete(model: OrderModel) {
        transaction(db) {
            model.item.delete()
        }
        orders.remove(model)
    }

    private fun delete(model: ShopModel) {
        transaction(db) {
            if (!Sale.find { Sales.buyerId eq model.item.id }.empty()) {
                throw ForeignKeyViolation("There is a sale referencing this shop.")
            }
        }

        transaction(db) {
            model.item.delete()
        }
        shops.remove(model)
    }

    fun <M, V : ItemViewModel<M>> commitDirty(dirtyMapping: Map<V, TableColumnDirtyState<V>>) {
        transaction(db) {
            dirtyMapping.filter {
                it.value.isDirty
            }.forEach {
                it.key.commit()
                it.value.commit()
            }
        }
    }

}

class ForeignKeyViolation(s: String) : Throwable(s)
